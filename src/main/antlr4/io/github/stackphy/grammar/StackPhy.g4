grammar StackPhy;

// Parser rules
program
    : statement* EOF
    ;

statement
    : functionDefinition
    | operation
    | value
    | comment
    ;

functionDefinition
    : ':' IDENTIFIER stackEffect? statement* ';'
    ;

stackEffect
    : '(' inputParams STACK_SEPARATOR outputDescription ')'
    ;
    
inputParams
    : (IDENTIFIER)*   // Zero or more input parameters
    ;

outputDescription
    : (IDENTIFIER | NUMBER | '*' | '+' | '-' | '/' | '.')*   // Specific tokens for output description
    ;

operation
    : dup
    | swap
    | drop
    | over
    | rot
    | nip
    | tuck
    | pick
    | '~'
    | '='
    | var
    | observe
    | '*'
    | '+'
    | '-'
    | '/'
    | mathOperation
    | distributionOperation
    | arrayOperation
    | functionOperation
    | constraintOperation
    | IDENTIFIER // Function call or variable reference
    ;

mathOperation
    : 'log'
    | 'exp'
    | 'sum'
    | 'product'
    | 'scale'
    | 'normalize'
    | 'vectorElement'
    | 'matrixElement'
    | 'negate'
    | 'sqrt'
    | 'pi'
    ;

functionOperation
    : 'sequence'               
    | 'mrca'                   
    | 'treeAge'
    | 'nodeAge'
    | 'branchLength'
    | 'distanceMatrix'
    | 'descendantTaxa'
    ;
    
constraintOperation
    : 'LessThan'
    | 'GreaterThan'
    | 'Equals'
    | 'Bounded'
    | 'SumTo'
    | 'Monophyly'
    | 'Calibration'
    | 'FixedTopology'
    | 'MolecularClock'
    | 'CompoundConstraint'
    | 'Correlation'
    ;

distributionOperation
    : 'Normal'
    | 'LogNormal'
    | 'Exponential'
    | 'Gamma'
    | 'Beta'
    | 'Dirichlet'
    | 'Uniform'
    | 'Yule'
    | 'BirthDeath'
    | 'Coalescent'
    | 'FossilBirthDeath'
    | 'JC69'
    | 'K80'
    | 'F81'
    | 'HKY'
    | 'GTR'
    | 'WAG'
    | 'JTT'
    | 'LG'
    | 'GY94'
    | 'DiscreteGamma'
    | 'DiscreteGammaVector'
    | 'FreeRates'
    | 'InvariantSites'
    | 'StrictClock'
    | 'UncorrelatedLognormal'
    | 'UncorrelatedExponential'
    | 'PhyloCTMC'
    | 'PhyloBM'
    | 'PhyloOU'
    ;

arrayOperation
    : '['
    | ']'
    ;
    
value
    : NUMBER
    | STRING
    ;

comment
    : COMMENT
    ;

// Lexer tokens for basic operations
dup : 'dup' ;
swap : 'swap' ;
drop : 'drop' ;
over : 'over' ;
rot : 'rot' ;
nip : 'nip' ;
tuck : 'tuck' ;
pick : 'pick' ;  // Added lexer token for "pick"
var : 'var' ;
observe : 'observe' ;

// Numbers
NUMBER : '-'? DIGIT+ ('.' DIGIT*)? | '-'? '.' DIGIT+ ;
fragment DIGIT : [0-9] ;

// Strings
STRING : '"' (~["\r\n] | '\\"')* '"' ;

// Identifiers
IDENTIFIER : [a-zA-Z_][a-zA-Z0-9_]* ;

// Comments
COMMENT : '//' ~[\r\n]* ;

STACK_SEPARATOR : '--' ;

// Whitespace - skip it in the lexer
WS : [ \t\r\n]+ -> skip ;