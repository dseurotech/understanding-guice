# Project purpose

The tests in this demo project are meant to explain common usage and corner cases of Guice binding, autowiring, etc.

# Running Order

This is the suggested running order:

* ## Provising:
    * _ProvidingStrategiesDemo_: showcases guice module examples, and different strategies to provide collaborators
    * _MoreSingletonExamples_: expands the use of Singleton providing
    * _SetProvidingStrategies_: on how to provide multiple implementations of an interface, grouped into sets
    * _NamedDemo_: when you have to provide multiple implementations of a common interface, and you need to choose which one to use
    * _ConcreteBindingDemo_: Demonstrate how to bind concrete classes, without relying on an interface
    * _ImplicitInjectionDemo_: Provided objects are automatically passed to other providing methods (a.k.a.: Linked Binding)
* ## Injection:
    * _ConstructorInjectionDemo_: Different constructor injection examples
    * _FieldInjectionDemo_: Different field injection examples
* ## Overrides:
    * _OverrideDemo_: how to override provided implementation by using modules override
* ## Stages:
    * _InstantiationTimeDemo_: When is a provided binding actually instantiated?
    * _WiringValidationDemo_: explains why you should always use Stage.PRODUCTION to validate your wiring