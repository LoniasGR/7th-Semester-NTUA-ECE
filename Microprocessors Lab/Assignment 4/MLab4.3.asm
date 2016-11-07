INCLUDE MACROS.ASM
;INCLUDE MACROS.TXT

STACK_SEG SEGMENT STACK
    DW 128 DUP(?)
ENDS

DATA_SEG SEGMENT
	ENTAILMENT DB " => $"
    LINE DB 0AH,0DH,"$"
    INPUT_MSG DB "GIVE UP TO 16 CHARACTERS (GIVE * TO EXIT)",0AH,0DH,"$"
    GIVE_MSG DB "<ENTER> TO CONFIRM INPUT:$"
	NUM_TABLE DB 20 DUP(?)
	LOWER_TABLE DB 20 DUP(?)
	UPPER_TABLE DB 20 DUP(?)
	NUM_COUNTER DW 0
	LOWER_COUNTER DW 0
	UPPER_COUNTER DW 0    
	OLD_MIN DB 0
	NEW_MIN DB 0
	INDEX DW 0
ENDS

CODE_SEG SEGMENT
    ASSUME CS:CODE_SEG,SS:STACK_SEG,DS:DATA_SEG,ES:DATA_SEG


MAIN PROC FAR
    ;SET SEGMENT REGISTERS
    MOV AX,DATA_SEG
    MOV DS,AX
    MOV ES,AX


START:
    PRINT_STRING INPUT_MSG
    PRINT_STRING GIVE_MSG
    MOV DH,3AH ; DH for old_min, DL for new_min
    MOV DL,3AH
    CALL INPUT_ROUTINE
	PRINT_STRING LINE
	CALL OUTPUT_ROUTINE
	PRINT_STRING LINE
	CMP DL,3AH
	JE SKIP_PRINT
	PRINT DH
	PRINT ' '
	PRINT DL     
	PRINT_STRING LINE
SKIP_PRINT:	
	MOV LOWER_COUNTER,0    ; RE-INIT
	MOV UPPER_COUNTER,0
	MOV NUM_COUNTER,0       
;	MOV AL,b.OLD_MIN
;	ADD AL,30H
;	PRINT AL
    JMP START
FEXIT:
    EXIT
MAIN ENDP

INPUT_ROUTINE PROC NEAR
    MOV CX,16	; MAX 16D NUMBERS
INPUT_LOOP: 
    READ        ; messes with AX, it's ok for now 
	CMP AL,20H		; SPACE
	JE SPACE_LOOP
	CMP AL,0DH		; ENTER
	JE INPUT_END
	CMP AL,2AH		; force exit
	JE FEXIT
    CMP AL,30H      ; '0'
    JL INPUT_LOOP 
    CMP AL,39H      ; '9'
    JG UPPER_CHECK  ; check for A-Z
	PRINT AL		; number 0-9
NUM_INPUT:
	MOV BX,OFFSET NUM_TABLE
	ADD BX,NUM_COUNTER		
	MOV [BX],AL		; Store number in table  
	CALL CHECK_MIN
	INC NUM_COUNTER	
	JMP ENDING_LOOP
UPPER_CHECK:
	CMP AL,41H
    JL INPUT_LOOP
    CMP AL,5AH
    JG LOWER_CHECK  ; check for a-z
	PRINT AL		; char A-Z 
UPPER_INPUT:
	MOV BX,OFFSET UPPER_TABLE
	ADD BX,UPPER_COUNTER		
	MOV [BX],AL		    ;Store Char in table
	INC UPPER_COUNTER
	JMP ENDING_LOOP
LOWER_CHECK:
	CMP AL,61H
    JL INPUT_LOOP
    CMP AL,7AH
    JG INPUT_LOOP  	; ignore it, go back
	PRINT AL		; char a-z
LOWER_INPUT:
	MOV BX,OFFSET LOWER_TABLE
	ADD BX,LOWER_COUNTER		
	MOV [BX],AL			;Store Char in table
	INC LOWER_COUNTER	
ENDING_LOOP:
	LOOP INPUT_LOOP
WAIT_FOR_ENTER:
	READ
	CMP AL,0DH	; enter
	JNE WAIT_FOR_ENTER
INPUT_END:
	PRINT_STRING ENTAILMENT
	RET 
SPACE_LOOP:
	PRINT ' '
	JMP ENDING_LOOP
INPUT_ROUTINE ENDP
                      

CHECK_MIN PROC NEAR
    CMP AL,DH      
    JG CNT
    CMP AL,DL
    JG JUSTOLDMIN  
    CMP DH,DL      ; O,N < #
    JNG NEWMIN     
    MOV DH,DL
NEWMIN:    
    MOV DL,AL
CNT:
    CMP AL,DL
    JG RETURN
    MOV DL,AL ; O < # < N
RETURN:
    RET
JUSTOLDMIN:   ; N < # < O
    MOV DH,DL
    MOV DL,AL            
    JMP RETURN          

OUTPUT_ROUTINE PROC NEAR
UPPER_START:
	MOV CX,UPPER_COUNTER
	CMP CX,0
	JE LOWER_START
	MOV BX,OFFSET UPPER_TABLE
UPPER_PRINT:
	MOV AL,DS:[BX]
	PRINT AL
	INC BX
	LOOP UPPER_PRINT  
    MOV BX,LOWER_COUNTER
    CMP BX,0
    JE LOWER_START
    MOV BX,NUM_COUNTER
    CMP BX,0
    JE NUM_START
    PRINT '-'
LOWER_START:                    
	MOV CX,LOWER_COUNTER
	CMP CX,0
	JE NUM_START
	MOV BX,OFFSET LOWER_TABLE
LOWER_PRINT:
	MOV AL,DS:[BX] 
	PRINT AL
	INC BX
	LOOP LOWER_PRINT
    MOV BX,NUM_COUNTER
    CMP BX,0
    JE NUM_START
    PRINT '-'
NUM_START:
	MOV CX,NUM_COUNTER
	CMP CX,0
	JE END_PRINT             
	MOV BX,OFFSET NUM_TABLE  
NUM_PRINT:
	MOV AL,DS:[BX]
	PRINT AL
	INC BX                      
	LOOP NUM_PRINT              
	;PRINT '-'
    
END_PRINT:
	RET
OUTPUT_ROUTINE ENDP
	

CODE_SEG ENDS

END MAIN