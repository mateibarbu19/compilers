# Zona de date
.data

print_one:
    .asciiz "1\n"
print_two:
    .asciiz "2\n"
print_large:
    .asciiz "large value\n"
print_small:
    .asciiz "small value\n"

# Zona de cod
.text

# Eticheta main este obligatorie.
main:
    li $v0 4            # print
    la $a0, print_one
    syscall

    li $v1 13
    ble $v1, 64, else

    li $v0 4            # print
    la $a0, print_large
    syscall

    j end


else:
    li $v0 4            # print
    la $a0, print_small
    syscall

end:
    li $v0 4            # print
    la $a0, print_two
    syscall

    li $v0 10           # exit
    syscall