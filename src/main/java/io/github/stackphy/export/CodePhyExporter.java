package io.github.stackphy.export;

import io.github.stackphy.distribution.*;
import io.github.stackphy.model.*;
import io.github.stackphy.runtime.Environment;
import io.github.stackphy.substitution.*;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import com.google.gson.*;

/**
 * Exports a StackPhy model to CodePhy JSON format.
 */
public class CodePhyExporter {
    private final Environment environment;
    private final Gson gson;
    
    /**
     * Creates a new CodePhy exporter for the given environment.
     * 
     * @param environment The environment containing the model to export
     */
    public CodePhyExporter(Environment environment) {
        this.environment = environment;
        this.gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    }
    
    /**
     * Exports the model to CodePhy JSON format.
     * 
     * @return The model in CodePhy JSON format
     */
    public String exportToJson() {
        JsonObject root = new JsonObject();
        
        // Add version
        root.addProperty("codephyVersion", "0.1");
        
        // Add model name (optional)
        root.addProperty("model", "StackPhy Export");
        
        // Add metadata
        addMetadata(root);
        
        // Add random variables
        addRandomVariables(root);
        
        // Add deterministic functions
        addDeterministicFunctions(root);
        
        return gson.toJson(root);
    }
    
    /**
     * Adds metadata to the JSON object.
     * 
     * @param root The root JSON object
     */
    private void addMetadata(JsonObject root) {
        JsonObject metadata = new JsonObject();
        
        metadata.addProperty("title", "Model exported from StackPhy");
        metadata.addProperty("description", "This model was automatically exported from a StackPhy script.");
        
        // Add created timestamp
        String timestamp = ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        metadata.addProperty("created", timestamp);
        metadata.addProperty("modified", timestamp);
        
        // Add software info
        JsonObject software = new JsonObject();
        software.addProperty("name", "StackPhy");
        software.addProperty("version", "0.1");
        software.addProperty("url", "https://github.com/yourusername/stackphy");
        metadata.add("software", software);
        
        root.add("metadata", metadata);
    }
    
    /**
     * Adds random variables to the JSON object.
     * 
     * @param root The root JSON object
     */
    private void addRandomVariables(JsonObject root) {
        JsonObject randomVariables = new JsonObject();
        
        // Get all stochastic variables from the environment
        Map<String, Variable> stochasticVars = environment.getStochasticVariables();
        
        for (Map.Entry<String, Variable> entry : stochasticVars.entrySet()) {
            String name = entry.getKey();
            Variable variable = entry.getValue();
            
            // Get the distribution
            Distribution distribution = variable.getDistribution();
            
            // Convert the distribution to CodePhy format
            JsonObject varJson = new JsonObject();
            JsonObject distJson = convertDistribution(distribution);
            
            varJson.add("distribution", distJson);
            
            // If the variable has observed data, add it
            if (variable.hasObservedData()) {
                JsonElement observedValue = convertObservedData(variable.getObservedData());
                if (observedValue != null) {
                    varJson.add("observedValue", observedValue);
                }
            }
            
            randomVariables.add(name, varJson);
        }
        
        root.add("randomVariables", randomVariables);
    }
    
    /**
     * Adds deterministic functions to the JSON object.
     * 
     * @param root The root JSON object
     */
    private void addDeterministicFunctions(JsonObject root) {
        JsonObject deterministicFunctions = new JsonObject();
        
        // Get all deterministic variables from the environment
        Map<String, Variable> deterministicVars = environment.getDeterministicVariables();
        
        for (Map.Entry<String, Variable> entry : deterministicVars.entrySet()) {
            String name = entry.getKey();
            Variable variable = entry.getValue();
            
            // Get the underlying value
            StackItem value = variable.getUnderlyingValue();
            
            // Convert to CodePhy format
            JsonObject funcJson = convertDeterministicFunction(value);
            if (funcJson != null) {
                deterministicFunctions.add(name, funcJson);
            }
        }
        
        root.add("deterministicFunctions", deterministicFunctions);
    }
    
    /**
     * Converts a distribution to CodePhy JSON format.
     * 
     * @param distribution The distribution to convert
     * @return The distribution in CodePhy JSON format
     */
    private JsonObject convertDistribution(Distribution distribution) {
        JsonObject distJson = new JsonObject();
        
        // Get distribution type
        String distributionType = distribution.getDistributionType();
        
        // Map the distribution type to the proper CamelCaps PhyloSpec type
        String phyloSpecType = mapDistributionTypeToPhyloSpec(distributionType);
        distJson.addProperty("type", phyloSpecType);
        
        // Set the generates property based on the distribution type
        if (distribution instanceof PhyloCTMC) {
            distJson.addProperty("generates", "Alignment");
        } else if (distribution instanceof Yule || 
                  distribution instanceof BirthDeath || 
                  distribution instanceof Coalescent) {
            distJson.addProperty("generates", "Tree");
        } else if (distribution instanceof Dirichlet) {
            distJson.addProperty("generates", "Vector");
        } else if (distribution instanceof DiscreteGamma) {
            distJson.addProperty("generates", "Vector");
        } else {
            distJson.addProperty("generates", "Real");
        }       
         
        // Add parameters based on the distribution type
        JsonObject parameters = new JsonObject();
        
        if (distribution instanceof LogNormal) {
            LogNormal logNormal = (LogNormal) distribution;
            parameters.add("meanlog", convertParameter(logNormal.getMean()));
            parameters.add("sdlog", convertParameter(logNormal.getStandardDeviation()));
        } 
        else if (distribution instanceof Normal) {
            Normal normal = (Normal) distribution;
            parameters.add("mean", convertParameter(normal.getMean()));
            parameters.add("sd", convertParameter(normal.getStandardDeviation()));
        } 
        else if (distribution instanceof Exponential) {
            Exponential exponential = (Exponential) distribution;
            parameters.add("rate", convertParameter(exponential.getRate()));
        } 
        else if (distribution instanceof Dirichlet) {
            Dirichlet dirichlet = (Dirichlet) distribution;
            JsonArray alpha = new JsonArray();
            Object[] concentrations = dirichlet.getConcentrationParameters().getArrayValue();
            for (Object concentration : concentrations) {
                if (concentration instanceof Number) {
                    alpha.add(((Number) concentration).doubleValue());
                }
            }
            parameters.add("alpha", alpha);
        } 
        else if (distribution instanceof DiscreteGamma) {
            DiscreteGamma discreteGamma = (DiscreteGamma) distribution;
            parameters.add("shape", convertParameter(discreteGamma.getShape()));
            parameters.add("categories", convertParameter(discreteGamma.getCategories()));
            
            // Add dimension parameter if it's a DiscreteGammaVector
            if (discreteGamma.getDimension() != null) {
                parameters.add("dimension", convertParameter(discreteGamma.getDimension()));
            }
        }
        else if (distribution instanceof Gamma) {
            Gamma gamma = (Gamma) distribution;
            parameters.add("shape", convertParameter(gamma.getShape()));
            parameters.add("rate", convertParameter(gamma.getRate()));
        } 
        else if (distribution instanceof Yule) {
            Yule yule = (Yule) distribution;
            parameters.add("birthRate", convertParameter(yule.getBirthRate()));
        } 
        else if (distribution instanceof BirthDeath) {
            BirthDeath birthDeath = (BirthDeath) distribution;
            parameters.add("birthRate", convertParameter(birthDeath.getBirthRate()));
            parameters.add("deathRate", convertParameter(birthDeath.getDeathRate()));
        } 
        else if (distribution instanceof Coalescent) {
            Coalescent coalescent = (Coalescent) distribution;
            parameters.add("populationSize", convertParameter(coalescent.getPopulationSize()));
        } 
        else if (distribution instanceof PhyloCTMC) {
            PhyloCTMC phyloCTMC = (PhyloCTMC) distribution;
            parameters.add("tree", convertParameter(phyloCTMC.getTree()));
            parameters.add("Q", convertParameter(phyloCTMC.getSubstitutionModel()));
            
            // Add optional parameters if present
            if (phyloCTMC.getSiteRates() != null) {
                parameters.add("siteRates", convertParameter(phyloCTMC.getSiteRates()));
            }
            if (phyloCTMC.getClockRate() != null) {
                parameters.add("rate", convertParameter(phyloCTMC.getClockRate()));
            }
        }
        
        distJson.add("parameters", parameters);
        
        return distJson;
    }
    
    /**
     * Maps a distribution type string from the internal representation to the PhyloSpec CamelCaps form.
     * 
     * @param distributionType The internal distribution type name
     * @return The PhyloSpec CamelCaps type name
     */
    private String mapDistributionTypeToPhyloSpec(String distributionType) {
        // Map of internal distribution types to PhyloSpec types
        Map<String, String> typeMap = new HashMap<>();
        typeMap.put("normal", "Normal");
        typeMap.put("logNormal", "LogNormal");
        typeMap.put("exponential", "Exponential");
        typeMap.put("gamma", "Gamma");
        typeMap.put("dirichlet", "Dirichlet");
        typeMap.put("beta", "Beta");
        typeMap.put("uniform", "Uniform");
        typeMap.put("yule", "Yule");
        typeMap.put("birthDeath", "BirthDeath");
        typeMap.put("coalescent", "Coalescent");
        typeMap.put("phyloCTMC", "PhyloCTMC");
        typeMap.put("phyloBM", "PhyloBM");
        typeMap.put("phyloOU", "PhyloOU");
        typeMap.put("discreteGamma", "DiscreteGamma");
        typeMap.put("discreteGammaVector", "DiscreteGammaVector");
        typeMap.put("freeRates", "FreeRates");
        typeMap.put("invariantSites", "InvariantSites");
        typeMap.put("strictClock", "StrictClock");
        typeMap.put("uncorrelatedLognormal", "UncorrelatedLognormal");
        typeMap.put("uncorrelatedExponential", "UncorrelatedExponential");
        
        // Return the mapped type if it exists, otherwise fallback to capitalized version
        return typeMap.getOrDefault(distributionType, 
               distributionType.substring(0, 1).toUpperCase() + distributionType.substring(1));
    }
    
    /**
     * Converts a deterministic function to CodePhy JSON format.
     * 
     * @param value The deterministic function value
     * @return The deterministic function in CodePhy JSON format, or null if not convertible
     */
    private JsonObject convertDeterministicFunction(StackItem value) {
        JsonObject funcJson = new JsonObject();
        
        // Handle different types of deterministic functions
        if (value instanceof HKY) {
            HKY hky = (HKY) value;
            funcJson.addProperty("function", "hky");
            
            JsonObject arguments = new JsonObject();
            arguments.add("kappa", convertParameter(hky.getKappa()));
            arguments.add("frequencies", convertParameter(hky.getBaseFrequencies()));
            
            funcJson.add("arguments", arguments);
            return funcJson;
        } 
        else if (value instanceof GTR) {
            GTR gtr = (GTR) value;
            funcJson.addProperty("function", "gtr");
            
            JsonObject arguments = new JsonObject();
            arguments.add("rates", convertParameter(gtr.getRateParameters()));
            arguments.add("frequencies", convertParameter(gtr.getBaseFrequencies()));
            
            funcJson.add("arguments", arguments);
            return funcJson;
        }
        
        // If we couldn't convert to a known function, return null
        return null;
    }
    
    /**
     * Converts a parameter to CodePhy JSON format.
     * 
     * @param param The parameter to convert
     * @return The parameter in CodePhy JSON format
     */
    private JsonElement convertParameter(Parameter param) {
        // If it's a Variable, create a reference
        if (param instanceof Variable) {
            Variable var = (Variable) param;
            JsonObject reference = new JsonObject();
            reference.addProperty("variable", var.getName());
            return reference;
        }
        
        // If it's a Primitive, convert the value directly
        if (param instanceof Primitive) {
            Primitive primitive = (Primitive) param;
            if (primitive.isNumeric()) {
                // Use the new isInteger() and isDouble() methods to determine the type
                if (primitive.isInteger()) {
                    // It's an integer, convert to int to avoid decimal point
                    return new JsonPrimitive((int)primitive.getDoubleValue());
                } else {
                    // It's a double, keep the decimal point
                    return new JsonPrimitive(primitive.getDoubleValue());
                }
            } else if (primitive.isString()) {
                return new JsonPrimitive(primitive.getStringValue());
            } else if (primitive.isArray()) {
                JsonArray array = new JsonArray();
                Object[] values = primitive.getArrayValue();
                for (Object value : values) {
                    if (value instanceof Parameter) {
                        // Use convertParameter recursively for nested Parameters
                        array.add(convertParameter((Parameter)value));
                    } else if (value instanceof Number) {
                        if (value instanceof Integer) {
                            // It's an integer, keep it as an integer
                            array.add((Integer)value);
                        } else {
                            // It's a floating-point number
                            array.add(((Number)value).doubleValue());
                        }
                    } else if (value instanceof String) {
                        array.add((String)value);
                    }
                }
                return array;
            }
        }
        
        // Default: convert to string
        return new JsonPrimitive(param.toString());
    }
    
    /**
     * Converts observed data to CodePhy JSON format.
     * 
     * @param data The observed data
     * @return The observed data in CodePhy JSON format, or null if not convertible
     */
    private JsonElement convertObservedData(StackItem data) {
        // If it's an array of sequences
        if (data instanceof Primitive && ((Primitive) data).isArray()) {
            Object[] sequences = ((Primitive) data).getArrayValue();
            if (sequences.length > 0 && sequences[0] instanceof Sequence) {
                JsonObject alignment = new JsonObject();
                
                // For each sequence, add a taxon entry
                for (Object seq : sequences) {
                    if (seq instanceof Sequence) {
                        Sequence sequence = (Sequence) seq;
                        alignment.addProperty(sequence.getTaxon(), sequence.getSequence());
                    }
                }
                
                return alignment;
            }
        }
        
        // For other types of data, try simple conversion
        return convertParameter((Parameter) data);
    }
}
