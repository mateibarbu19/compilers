# Zona de cod
.text

.data
newline:
    .asciiz "\n"

# Eticheta main este obligatorie.
main:
    li $a0 20

loop:
    ble $a0, 0, end

    li $v0 1           # print_int
    syscall

    # move $t1 $a0
    # li $v0 4           # print_newline
    # la $a0, newline
    # syscall

    addiu $a0 $a0 -1

    j loop

end:
    li $v0 10           # exit
    syscall