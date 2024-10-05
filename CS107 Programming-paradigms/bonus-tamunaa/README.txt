_________________________SCHEME INTERPRETER_________________________

INTRODUCTION
    This is a simple Scheme interpreter written in python.
    It allows users to enter Scheme expressions and evaluate them.

Requirements
    To use this interpreter, you need to have python installed on your system.

USAGE
    To use this interpreter, you need to run the program
    and enter valid scheme expressions (you can see some examples
    in the end of this file)

Limitations
    This interpreter is not a full-fledged Scheme implementation and
    may not support all features of the language.
    (if you want to define variable, you should
    write "defun" instead of define)





----------------------------EXAMPLES----------------------------

(+ 1 2 3 4 5) //15
(- 10 1 2 3 4) //0
(* 1 2 3) //6
(/ 1 2) //0


(defun x 1)
(defun y 2)
(define (sum x y) (+ x y))
(sum x y)

(define (sum ls)(if (null? ls) 0(+ (car ls) (sum (cdr ls)))))
(define (fibonacci n) (if (< n 2) n (+ (fibonacci (- n 1)) (fibonacci (- n 2)))))
(define (factorial n)(if (zero? n) 1(* n (factorial (- n 1)))))
(define (triple-everything numbers)(if (null? numbers) (quote ()) (cons (* 3 (car numbers)) (triple-everything (cdr numbers))))))


(defun ls '(1 2 3 4 5))
(apply + ls)
(apply - ls)
(apply * ls)
(apply / ls)

(sum ls)
(map factorial ls)
(map fibonacci ls)
(map triple-everything ls)
(map (lambda x (+ x 1)) ls)

(defun one '(1 2 3))
(defun two '(1 2 3))
(define (dot-product one two) (if (null? one) 0 (+ (* (car one) (car two))(dot-product (cdr one) (cdr two)))))
