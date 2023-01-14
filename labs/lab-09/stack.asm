.data
newline:
    .asciiz "\n"

# Zona de cod
.text


# Eticheta main este obligatorie.
main:
    li $a0 20

loop_1:
    ble $a0, 0, loop_2

    sw $a0, 0($sp)
    addi $sp, $sp, -4

    addiu $a0 $a0 -1

    j loop_1

loop_2:

    bge $a0, 21, end

    lw $a0, 4($sp)
    addi $sp, $sp, 4

    li $v0 1           # print_int
    syscall

    move $t1 $a0
    li $v0 4           # print_newline
    la $a0, newline
    syscall

    addiu $a0 $t1 1

    j loop_2

end:
    li $v0 10           # exit
    syscall