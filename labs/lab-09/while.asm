.data
newline:
    .asciiz "\n"

# Zona de cod
.text


# Eticheta main este obligatorie.
main:
    li $a0 20

loop:
    ble $a0, 0, end

    li $v0 1           # print_int
    syscall

    move $t1 $a0
    li $v0 4           # print_newline
    la $a0, newline
    syscall

    addiu $a0 $t1 -1

    j loop

end:
    li $v0 10           # exit
    syscall