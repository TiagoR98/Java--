@echo off
echo jjtree - - - - - - - - - - - - - - - - - 
echo - - - -  - - - - - - - - - - - - - - - - -
CALL jjtree NewJava.jjt
echo - - - - - - - - - - - - - - - - - - - - - 
echo javacc - - - - - - - - - - - - - - - - - -
echo - - - -  - - - - - - - - - - - - - - - - -
CALL javacc NewJava.jj
echo javac - - - - -- - - - - - - -  - - - - -
echo - - - -  - - - - - - - - - - - - - - - - -
CALL javac *.java
echo execution - - - - - - - - - - - - - - - -
echo - - - -  - - - - - - - - - - - - - - - - -
CALL java Main exemplo2.jmm exemplo1.jmm