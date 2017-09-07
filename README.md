# Persisting Android Material Stepper [![Release](https://jitpack.io/v/LucasR93/persisting-android-material-stepper.svg)](https://jitpack.io/#LucasR93/persisting-android-material-stepper.svg)

This library tries to merge the variable persistence from [WizardDroid](https://github.com/Nimrodda/WizarDroid) into  [Android Material Stepper](https://github.com/stepstone-tech/android-material-stepper)



## Documentation
Instead of extending from Fragment and including Step simply extend from StepFragment class

Use annotation @StepVariable for variables defined in your StepFragments or PersistingStepperLayout. Variables are automatically shared into PersistingStepperLayout and the following StepFragment upon clicking next in the stepper.
Make sure to use the same name for StepVariables across different StepFragments.