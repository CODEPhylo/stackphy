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

// Stack effect notation - proper parsing of inputs and outputs
stackEffect
    : '(' inputParams '--' outputDescription ')'
    ;

inputParams
    : IDENTIFIER (IDENTIFIER)*   // One or more input parameters
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

// Whitespace - skip it in the lexer
WS : [ \t\r\n]+ -> skip ;