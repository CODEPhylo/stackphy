package io.github.stackphy.model;

/**
 * Represents sequence data.
 */
public class Sequence implements StackItem {
    private final String taxon;
    private final String sequence;
    
    /**
     * Creates a new sequence.
     * 
     * @param taxon The taxon (organism) name
     * @param sequence The DNA/RNA/protein sequence
     */
    public Sequence(String taxon, String sequence) {
        if (taxon == null || taxon.isEmpty()) {
            throw new IllegalArgumentException("Taxon name cannot be null or empty");
        }
        if (sequence == null || sequence.isEmpty()) {
            throw new IllegalArgumentException("Sequence cannot be null or empty");
        }
        
        this.taxon = taxon;
        this.sequence = sequence;
    }
    
    /**
     * Gets the taxon (organism) name.
     * 
     * @return The taxon name
     */
    public String getTaxon() {
        return taxon;
    }
    
    /**
     * Gets the sequence data.
     * 
     * @return The sequence string
     */
    public String getSequence() {
        return sequence;
    }
    
    @Override
    public StackItemType getType() {
        return StackItemType.SEQUENCE;
    }
}
