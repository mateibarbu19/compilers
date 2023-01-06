# Zona de date
.data

welcome_string:
    .asciiz "Hello, World!\n"


# Zona de cod
.text

# Eticheta main este obligatorie.
main:
    li $v0 4            # print
    la $a0, welcome_string
    syscall

    li $v0 10           # exit
    syscall
