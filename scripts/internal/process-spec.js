/**
 * Daniel Hougaard, Infisical Inc 2024
 * *** DO NOT MODIFY ***
 *
 * This file is used to parse our OpenAPI Spec gotten from (https://app.infisical.com/api/docs/yaml).
 * By default, the codegen simply won't work with our spec due to issues with the codegen itself.
 * We need to parse out UUID formattings, and remove all _id attributes. _id are a legacy part of Infisical that only exist to this day to keep backwards compatability.
 *
 *
 * DO NOT modify the generation logic. Modify the `formatter.js` file instead, if you need to add new API paths to the codegen.
 */

const fs = require('fs');
const yaml = require('js-yaml');


// Removes duplicate keys in YAML, but preserve the structure of objects
function loadYamlWithDuplicates(yamlContent) {
    const parsedContent = yaml.load(yamlContent, { json: true });

    // parsing without first cleaning causes an error on-load due to supposedly duplicate keys. We resolve this by looping over the spec and removing all duplicate fields
    const cleanYaml = (obj) => {
        if (typeof obj === 'object' && obj !== null) {
            // If it's an object, iterate over its properties
            const uniqueKeys = new Set(); // To track already seen keys
            const cleanedObj = {};

            for (const [key, value] of Object.entries(obj)) {
                // if the key already exists, we keep the first one
                if (uniqueKeys.has(key)) {
                    continue;
                } else {
                    uniqueKeys.add(key);
                    cleanedObj[key] = value;
                }
            }

            return cleanedObj;
        } else {
            // Return the value as it is if it's not an object
            return obj;
        }
    };

    // Clean the parsed content recursively
    return cleanYaml(parsedContent);
}

// Recursively removes all occurrences of `format: uuid` from the YAML object.
// The codegen doesn't support UUID's so we need to treat it like regular strings.
function removeUuidFormat(obj) {
    if (Array.isArray(obj)) {
        // If it's an array, recursively process each element
        return obj.map(removeUuidFormat);
    } else if (typeof obj === 'object' && obj !== null) {
        // If it's an object, iterate through its keys
        const result = {};

        for (const [key, value] of Object.entries(obj)) {
            if (key === 'format' && value === 'uuid') {
                // skip entry (remove the format: uuid)
                continue;
            }
            // recursively loop over the entire spec
            result[key] = removeUuidFormat(value);
        }

        return result;
    }

    return obj;
}



// Recursively removes all occurrences of `_id` from the YAML object.
// This is because _id will conflict with the ID field due to some weirdness internally in the codegen, amazing right?
function removeIdField(obj) {
    if (Array.isArray(obj)) {
        // if it's an array, recursively process each element (can happen when it's listed as a required field)
        return obj.filter(item => item !== '_id').map(removeIdField);
    } else if (typeof obj === 'object' && obj !== null) {
        const result = {};

        for (const [key, value] of Object.entries(obj)) {
            if (key === '_id') {
                // skio the `_id` key
                continue;
            }

            result[key] = removeIdField(value);
        }

        return result;
    }

    return obj;
}

// filters the actual spec so we can use it for codegen
function filterOpenAPI(inputFile, outputFile, pathsToKeep) {
    console.log("Formatting OpenAPI Spec")
    // Read the input YAML file

    const yamlContent = fs.readFileSync(inputFile, 'utf8');

    // Load the YAML content, removing duplicate keys
    const spec = loadYamlWithDuplicates(yamlContent);

    // Filter the paths based on the pathsToKeep array
    const filteredPaths = {};
    pathsToKeep.forEach(path => {
        if (spec.paths && spec.paths[path]) {
            filteredPaths[path] = spec.paths[path];
        }
    });

    // Set the filtered paths to the spec
    spec.paths = filteredPaths;

    // Remove all occurrences of `format: uuid`
    const cleanedSpec = removeIdField(removeUuidFormat(spec))

    // Write the filtered spec to the output YAML file
    fs.writeFileSync(outputFile, yaml.dump(cleanedSpec, { sortKeys: false, lineWidth: -1 }));

    console.log("Successfully formatted OpenAPI Spec")
}

// keeping these here to keep things modular
const INPUT_FILE = "../src/main/resources/api.yaml"
const OUTPUT_FILE = "../src/main/resources/filtered-api.yaml"



module.exports = (paths) => {
    filterOpenAPI(INPUT_FILE, OUTPUT_FILE, paths)
}