/*
Copyright 2016 StepStone Services

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package de.lucasreiners.persistingstepper;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.AttrRes;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.FloatRange;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.annotation.UiThread;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.stepstone.stepper.BlockingStep;
import com.stepstone.stepper.R;
import com.stepstone.stepper.Step;
import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;
import com.stepstone.stepper.adapter.StepAdapter;
import com.stepstone.stepper.internal.feedback.StepperFeedbackType;
import com.stepstone.stepper.internal.feedback.StepperFeedbackTypeFactory;
import com.stepstone.stepper.internal.type.AbstractStepperType;
import com.stepstone.stepper.internal.type.StepperTypeFactory;
import com.stepstone.stepper.internal.util.AnimationUtil;
import com.stepstone.stepper.internal.util.TintUtil;
import com.stepstone.stepper.internal.widget.ColorableProgressBar;
import com.stepstone.stepper.internal.widget.DottedProgressBar;
import com.stepstone.stepper.internal.widget.RightNavigationButton;
import com.stepstone.stepper.internal.widget.TabsContainer;
import com.stepstone.stepper.viewmodel.StepViewModel;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Date;

import de.lucasreiners.persistingstepper.annotations.StepVariable;

/**
 * Stepper widget implemented according to the <a href="https://www.google.com/design/spec/components/steppers.html">Material documentation</a>.<br>
 * It allows for setting three types of steppers:<br>
 * - mobile dots stepper,<br>
 * - mobile progress bar stepper,<br>
 * - horizontal stepper with tabs.<br>
 * Include this stepper in the layout XML file and choose a stepper type with <code>ms_stepperType</code>.<br>
 * Check out <code>values/attrs.xml - StepperLayout</code> for a complete list of customisable properties.
 */
public class PersistingStepperLayout extends StepperLayout {

    private Bundle variableStore = new Bundle();

    public PersistingStepperLayout(Context context) {
        super(context);
    }

    public PersistingStepperLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PersistingStepperLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onNext() {
        Step step = findCurrentStep();

        //Persist the variables in the store
        persistStepVariables(step);

        //Fill the variables into the next step
        Step nextStep = findNextStep();
        if(nextStep != null){
            nextStep.loadStepVariables(getStepVariables());
        }

        //Fill the variables into the Layout
        loadStepVariables(getStepVariables());

        //Finally call super
        super.onNext();
    }

    @Override
    public void onComplete() {
        Step step = findCurrentStep();

        persistStepVariables(step);

        //Fill the variables into the Layout
        loadStepVariables(getStepVariables());

        //Finally call super
        super.onComplete();
    }

    public Bundle getStepVariables(){
        return variableStore;
    }

    private void persistStepVariables(Step step) {
        Field[] fields = step.getClass().getDeclaredFields();
        for (Field field : fields) {
            StepVariable contextVar = field.getAnnotation(StepVariable.class);
            if (contextVar != null) {
                //Store its value in the Wizard Context
                field.setAccessible(true);
                try {
                    if (field.getType() == String.class) {
                        variableStore.putString(field.getName(), (String) field.get(step));
                    }
                    else if (field.getType() == Integer.class) {
                        variableStore.putInt(field.getName(), field.getInt(step));
                    }
                    else if (field.getType() == Boolean.class) {
                        variableStore.putBoolean(field.getName(), field.getBoolean(step));
                    }
                    else if (field.getType() == Double.class) {
                        variableStore.putDouble(field.getName(), field.getDouble(step));
                    }
                    else if (field.getType() == Float.class) {
                        variableStore.putFloat(field.getName(), field.getFloat(step));
                    }
                    else if (field.getType() == Short.class) {
                        variableStore.putShort(field.getName(), field.getShort(step));
                    }
                    else if (field.getType() == Byte.class) {
                        variableStore.putByte(field.getName(), field.getByte(step));
                    }
                    else if (field.getType() == Long.class) {
                        variableStore.putLong(field.getName(), field.getLong(step));
                    }
                    else if (field.getType() == Character.class) {
                        variableStore.putChar(field.getName(), field.getChar(step));
                    }
                    else if (Parcelable.class.isAssignableFrom(field.getType())) {
                        variableStore.putParcelable(field.getName(), (Parcelable) field.get(step));
                    }
                    else if (field.getType() == Date.class) {
                        Date d = (Date) field.get(step);
                        if (d != null) {
                            variableStore.putLong(field.getName(), d.getTime());
                        }
                    }
                    else if (field.getType() instanceof Serializable) {
                        variableStore.putSerializable(field.getName(), (Serializable) field.get(step));
                    }
                    else {
                        throw new RuntimeException(String.format("Unsuported type. Cannot pass value to variable %s of step %s. Variable type is unsuported.",
                                field.getName(), step.getClass().getName()));
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void loadStepVariables(Bundle args) {
        //Scan the StepperLayout for fields annotated with @StepVariable
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
