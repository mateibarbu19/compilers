.data
newline:
    .asciiz "\n"
comma:
    .asciiz ","

# Zona de cod
.text


# Eticheta main este obligatorie.
main:
    li $a0 12

    li $v0 1           # print_int
    syscall

    li $a1, 2

loop:
    beq $a0, 1, end

    div	$a0, $a1
    mfhi $a3
    mflo $a2

    bne $a3, 0, else

    j rest

else:

    li $a2, 3
    mult $a0, $a2
    mflo $a2
    addi $a2, 1

rest:
    li $v0 4           # print_comma
    la $a0, comma
    syscall

    move $a0 $a2
    li $v0 1           # print_int
    syscall

    j loop

end:
    li $v0 4           # print_newline
    la $a0, newline
    syscall

    li $v0 10           # exit
    syscall