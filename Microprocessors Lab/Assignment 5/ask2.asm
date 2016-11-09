; IN THE FIRST PLACE OF EVERY ARRAY IN DATA_SEG WE STORE THE NUMBER OF ELEMENTS IN THE ARRAY

DATA_SEG SEGMENT
    PATHNAMELEN EQU 50
    
    ;input file
    SFILEBUFFER DB PATHNAMELEN      ;max length of filename
    SBYTESTYPED DB 0                ;length of filename
    SFILENAME DB PATHNAMELEN DUP(0) ;filename
    SLASTBYTE DB 0
    
        
    ;output file
    DFILEBUFFER DB PATHNAMELEN      ;max length of filename
    DBYTESTYPED DB 0                ;length of filename
    DFILENAME DB PATHNAMELEN DUP(0) ;filename
    DLASTBYTE DB 0

    ROTATIONS   EQU 1000H
    SAFE_HAVEN  EQU 1500H 
    TEMPOBUFFER EQU 1520H
    BIGBUFFER   EQU 1530H
    
    
    ;displayed messages
    NEW_LINE DB 0AH,0DH,'$'
    MESS0 DB "GIVE K NUMBER: ",'$'
    MESS1 DB "SOURCE FILE PATHNAME: " ,'$'
    MESS2 DB "DESTINATION FILE PATHNAME: " ,'$'   
    
    ;file handlers
    SHANDLE DW 0
    DHANDLE DW 0
    
    
DATA_SEG ENDS
;----------------------------------------------------------------               
CLEAN MACRO   
    MOV CX,0       
CLEANLOOP1:                  
    MOV DI,BIGBUFFER  
    ADD DI,CX
    MOV DS[DI],00H
    INC CX
    CMP CL,11H
    JL CLEANLOOP1       
ENDM
PRINT MACRO CHAR
    PUSH AX
    PUSH DX
    MOV DL,CHAR
    MOV AH,2
    INT 21H
    POP DX
    POP AX
    
ENDM
READ MACRO
    MOV AH,8
    INT 21H
ENDM
EXIT MACRO
    MOV AX,4C00H
    INT 21H
ENDM
PRINT_STRING MACRO STRING
    PUSH AX
    MOV DX,OFFSET STRING
    MOV AH,9
    INT 21H
    POP AX
ENDM
OUR_SAVIOR MACRO
    MOV AL,DL               ;in DL we have the just printed element
    MOV DI,SAFE_HAVEN          ;SAFE_HAVEN is used for storing in file
    MOV DS[DI],AL
    PUSH AX
    PUSH BX
    PUSH CX
    PUSH DX
    MOV AH,40H                     ;
    MOV BX,DHANDLE                 ; arguments for INT21
    MOV DX,OFFSET SAFE_HAVEN       ;
    MOV CX,1
    INT 21H
    POP DX
    POP CX
    POP BX                         ; it's a macro we don't want changes 
    POP AX
ENDM
    
;++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

CODE SEGMENT PUBLIC
        ASSUME CS:CODE_SEG,DS:CODE_SEG  
    
MAIN PROC NEAR    
    MOV AX,DATA_SEG
    MOV DS,AX                       ;;;; pretty much pages 386-387
    
    PRINT_STRING MESS0
    CALL NUM_INPUT                  
    MOV DI,ROTATIONS
    MOV DS[DI],AL                   ; rotations stored in memory @ 1000h
    
    PRINT_STRING NEW_LINE
    PRINT_STRING MESS1                 ; give input
    MOV AH,0AH
    MOV DX,OFFSET SFILEBUFFER
    INT 21H
    PRINT_STRING NEW_LINE                                        
    
    MOV BX,OFFSET SFILENAME         
    MOV AL,[SBYTESTYPED]
    XOR AH,AH
    ADD BX,AX
    MOV BYTE PTR[BX],00             ; we set the last character to be 0 (asciiz)
    
    PRINT_STRING MESS2                 ; give output
    MOV AH,0AH                      
    MOV DX,OFFSET DFILEBUFFER
    INT 21H
    MOV BX,OFFSET DFILENAME         
    MOV AL,[DBYTESTYPED]
    XOR AH,AH
    ADD BX,AX
    MOV BYTE PTR[BX],00             ; we set the last character to be 0 (asciiz)
    
    MOV DX,OFFSET SFILENAME         
    MOV AX,3D00H                    ; read file code
    INT 21H
    JC FINISH
    MOV SHANDLE,AX
    
    MOV DX,OFFSET DFILENAME         
    MOV CX,0000H
    MOV AX,3C00H	                ; output file code
    INT 21H
    JC FINISH
    MOV DHANDLE,AX                  ; in AX is the handler stored -> dhandle
    PRINT_STRING NEW_LINE   
    
MAKE_CLEAN:                         ; we get here everytime we finish a line
    CLEAN
ADDR:    
    MOV AH,3FH                      ; read a char from shandle file
    MOV CX,1
    MOV DX,OFFSET TEMPOBUFFER
    MOV BX,SHANDLE
    INT 21H
    CMP AX,00H                      ; no characters left to read, EOF -> finish
    JE FINISH
    MOV DI,TEMPOBUFFER
    MOV AL,DS[DI]
    CMP AL,0DH                      ; change of line ??
    JE LINE_CHANGE                  ;        then print the current line (chars read until 0Dh) to stdout & file
                                    
                                    
    CALL STORE_NEW                 ; store in BIGBUFFER
    JMP ADDR
LINE_CHANGE:
    PRINT_STRING NEW_LINE
    MOV DI,BIGBUFFER                   ; address of BIGBUFFER array
    CALL PRINT_CURR_LINE            ; we print the newly read line no matter what 
    ; PUSH DX    
    MOV DI,ROTATIONS
    MOV BL,DS[DI]                   ; K is in BL
    
    MOV DI,BIGBUFFER                   ; first position of the array
    MOV CL,DS[DI]                   ; cl = num_of_chars
    
    CMP BL,CL
    ; POP DX                       
    JG  NADA                        
    JMP ROTATEnPRINT                ; if K > num_of_chars, we assume that we shouldn't rotate the file 
NADA:                               ; but print it like it is
    MOV BX,00H   
GIST:    
    MOV DI,BIGBUFFER
    INC DI
    ADD DI,BX
    MOV AL,DS[DI]
    
    CMP AL,00H                      ; IF EOL -> next line
    JE NL_OUT
    
    MOV AH,02H                      
    MOV DL,DS[DI]
    INT 21H                         ; print char from output
    OUR_SAVIOR                        
    INC BL
    JMP GIST
    
    
ROTATEnPRINT:
    MOV DI,ROTATIONS
    MOV CL,DS[DI]                   ; cl = rotations
    MOV CH,00H
    MOV DI,BIGBUFFER
    MOV BL,DS[DI]                   ; BL = NUMOFCHARS
    SUB BL,CL
    MOV CL,BL                       ; print with this order: 
    MOV BX,0000H                    ; (num_of_chars - rotations) to (num_of_chars) and then 
CURR:                               ; from 2 to (num_of_chars - rotations - 1)
    MOV DI,BIGBUFFER
    INC DI
    
    ADD DI,BX
    ADD DI,CX                       ; (num_of_chars - rotations)
    MOV AL,DS[DI]
    
    
    CMP AL,00H
    JE CONT                         ; null
    
    MOV AH,02H                      ; print char from output
    MOV DL,DS[DI]
    INT 21H
    OUR_SAVIOR                        
    INC BL
    JMP CURR
    
CONT:
    MOV DI,ROTATIONS
    MOV CL,DS[DI]
    MOV CH,00H
    
    MOV DI,BIGBUFFER
    MOV BL,DS[DI]
    SUB BL,CL
    
    MOV CL,BL
    MOV BX,0000H
                            
L2: 
    MOV DI,BIGBUFFER
    INC DI
    
    ADD DI,BX
    
    MOV AH,02H
    MOV DL,DS[DI]
    INT 21H
    OUR_SAVIOR
    INC BL
    CMP BL,CL                       
    JNE L2
    
NL_OUT:
    PRINT_STRING NEW_LINE
    MOV DL,0DH                     
    OUR_SAVIOR                        ; newline in output file
    MOV DL,0AH
    OUR_SAVIOR
        
    JMP MAKE_CLEAN                  
FINISH:
    MOV AH,3EH                      ;close input file
    MOV BX,SHANDLE
    INT 21H
    MOV AH,3EH
    MOV BX,DHANDLE                  ;close output file
    INT 21H
    POP DS
    MOV AH,4CH
    INT 21H
    EXIT
MAIN ENDP

;++++++++++++++ END OF MAIN PROGRAM +++++++++++++++++++++++++++++
            
NUM_INPUT PROC NEAR
    PUSH DX
IGNORE:
    READ
    CMP AL,30H
    JL IGNORE
    CMP AL,39H                      ; range : 1-9 
    JG IGNORE 
    PRINT AL
    SUB AL,30H
    POP DX
    RET
NUM_INPUT ENDP

PRINT_CURR_LINE PROC NEAR  
    PUSH AX
    PUSH CX                         ; the address of the first element of BIGBUFFER is currently stored in DI
    MOV AH,02H
    MOV CL,DS[DI]                    ; first elemtn = numofchars
                      
    INC DI
LOOPRINT:
    MOV DL,DS[DI]   
    INT 21H
    DEC CL 
    INC DI                          ; print CL elements
    CMP CL,00H
    JGE LOOPRINT      
    PRINT_STRING NEW_LINE
    POP CX
    POP AX
    RET
PRINT_CURR_LINE ENDP 

STORE_NEW PROC NEAR                ; based on the first element, which contains the number of read chars, 
    MOV DI,TEMPOBUFFER                  ; go and place AL in the next free element
    MOV AL,DS[DI]                    
    CMP AL,0AH
    JE ENDD                              ; STORES IN BIGBUFFER
         
    MOV DI,BIGBUFFER
    MOV BL,DS[DI]                   
                                    
    MOV BH,00H
    ADD DI,BX                       
    INC DI
    MOV DS[DI],AL                  
    INC BL                          ; num_of_chars++
    MOV DI,BIGBUFFER
    MOV DS[DI],BL                   
ENDD:
    RET
STORE_NEW ENDP
            
CODE ENDS 
END MAIN
