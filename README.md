h3. Project Summary

Eclipse DSM-Viewer plugin is a simple (but very powerful) tool for counting, analysing and displaying of all kinds of dependencies which can be determined within the Java code.

In simple words, it founds and counts any relationships between the components of Java program systems (packages, classes, ...) to display them in the special format - in format of Dependency Structure Matrices (DSMs). This plugin also provides all kinds of Eclipse integration such as code navigation, help pages, and so on.

Our idea is to create native to Eclipse plugin with free license to let all Eclipse developers to encorporate DSM analysis during development but not as "after commit" analysis as this can be done using Maven-DSM-plugin. In first milestones we will focus on UI presentation, DSM structure will be taken from opensource library dtangler, additionally we will focus on convenience to use in Eclipse (especially on navigation from matrix to real code). Usage of dtangler library was already succesfuly tested in our DSM-Maven-plugin.

h3. Dependencies:

"DTangler": http://web.sysart.fi/dtangler/
"Nat Table (Nebula widgets)": http://www.eclipse.org/nattable/

h3. Author

Daniil Yaroslavtsev (https://github.com/daniilyar)

h3. Curator

Roman Ivanov (https://github.com/romani) 

h3. Frequently Asked Questions

1. What is "relationship" (or "dependency") in terms of Java?

All programming systems are written on Java consists of standard components (projects, packages, classes, methods, and so on). All these components can be dependent one from another with many kinds of relationships. For example, all situations below establishing unidirectional relationships (i.e. dependencies) between the components of Java code:

Object creation. By writing "Object s = new MyObj();" within the Java class, you`ll create the new dependency (dependency between creator class and created object).

Method calls. "SomeClass.callSomething()" is written within the Java class body
establishes the relationship between current class (method caller) and callee class. Same for non-static method calls.

Inheritance. The line "Sometype extends anotherType" also establishes a relationship.
And so on (implementing Java interfaces, accessing static fields, accessing external classes with “ClassName.class”, etc.)
Hereby, whenever you getting access to the one component of Java code from another, you creating the unidirectional relationship between these components. We call all such relationships as "dependencies".

2. Why do we need the such kinds of dependency measuring / visualization tools?

Large set of dependencies between the modules of the program systems (espesially complex dependencies and cyclic dependencies between them) makes the code of these modules more difficult to understand or refactor because of high coupling level between the separate modules. So, the idea of high-cohesion inside a system modules and low-coupling between these modules is a "new trend" and very popular in nowadays. 

But if you cannot measure and display dependencies correctly, it is hard to get a concrete evaluation of cohesion and coupling.

There are two popular ways to display dependencies between the components of program systems (and, sure, Java programming systems).

The first and the most commonly used way is to present them in the form of an oriented graph(s). In this way, system`s components becomes the graph`s nodes meanwhile the number/types of dependencies becomes the weights on graph`s edges.

The second approach is to display dependencies as a special kinds of incidence matrices is called DSMs or Dependency Structure Matrices.

3. What is a "Dependency Structure Matrix" and why this format is useful for dependencies displaying/analyzing?

DSM is a simple and compact visual representation of a system or project. It is a square matrix such as below:
![Example DSM](https://raw.github.com/daniilyar/Eclipse-DSM-viewer/gh-pages/images/exampleDSM.png)

This example DSM is drawn for imaginary system (or project) which consists of three modules: A, B and C. Moreover, we can see that it is a second module (B) consits of three submodules: D, E and F. Green cells in this table displays the dependencies. 
We can see on given DSM that the example system consists of Module A, Module B and Module C. Module A is dependent only from module C (see the first matrix line) and the module B is fully-independent as it has only internal dependencies. In the same way, the 2 submodules of B (E and F) are dependent from the third it`s submodule (D).
Above you can see the graph representation of our example system:
![Example Graph](https://raw.github.com/daniilyar/Eclipse-DSM-viewer/gh-pages/images/exampleGRAPH.png)

As we can see, both graphs and DSMs are useful for displaying code dependencies, but there are some nuances:
If you want to analyze dependencies between components of the simple programming systems, graph representation is more clear and intuitive. Otherwise, if you want to analyze dependencies for more complex systems, displaying them in form of a graph is not so clear and comfortable. When the number of graph nodes and edges grows, the graph representation becomes very complex for understanding. The graph with more than 50 nodes (and 200+edges) becomes almost unreadable for an average person.
DS-matrices solves such readability problems. DS-matrix can be human-readable in case of very large sets of code (and, as a result, large sets of code dependencies) is being analysed.
Real DS-matrices / graphs are many times larger and really more complex than our simplified example. Modern Java applications contains thousands or even millions dependencies. 10 000 classes and more is really not a limit for modern complex Java applications. Therefore, the DSM representation can be more convenient and useful for dependency analysing tasks are needed in modern Java programming systems.
