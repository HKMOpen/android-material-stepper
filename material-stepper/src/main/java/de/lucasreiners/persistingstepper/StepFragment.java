package de.lucasreiners.persistingstepper;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.stepstone.stepper.Step;

import java.lang.reflect.Field;
import java.util.Date;

import de.lucasreiners.persistingstepper.annotations.StepVariable;

/**
 * Created by lucasreiners on 07.09.17.
 */

public abstract class StepFragment extends Fragment implements Step, StepVariableCallback {

    @Override
    public void onStepVariablesChanged() {

    }

    @Override
    public final void loadStepVariables(Bundle args) {
        //Scan the step for fields annotated with @StepVariable
        //and bind value if found in step's arguments
        Field[] fields = this.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.getAnnotation(StepVariable.class) != null && args.containsKey(field.getName())) {
                field.setAccessible(true);
                try {
                    if (field.getType() == Date.class) {
                        field.set(this, new Date(args.getLong(field.getName())));
                    }
                    else {
                        field.set(this, args.get(field.getName()));
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
