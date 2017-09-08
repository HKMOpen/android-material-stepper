package com.stepstone.stepper.internal.type;

import android.support.annotation.NonNull;
import android.support.annotation.RestrictTo;
import android.view.View;
import android.widget.RelativeLayout;

import com.stepstone.stepper.R;
import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.adapter.StepAdapter;
import com.stepstone.stepper.internal.widget.ColorableProgressBar;

import static android.support.annotation.RestrictTo.Scope.LIBRARY;

/**
 * Created by hesk on 8/9/2017.
 */
@RestrictTo(LIBRARY)
public class TopProgressBarStepperType extends AbstractStepperType {

    private final ColorableProgressBar mProgressBar;
    private final RelativeLayout mBottomNagivation_2, mBottomNagivation_1;

    public TopProgressBarStepperType(StepperLayout stepperLayout) {
        super(stepperLayout);
        mProgressBar = (ColorableProgressBar) stepperLayout.findViewById(R.id.ms_stepTopProgressBar);
        mBottomNagivation_1 = (RelativeLayout) stepperLayout.findViewById(R.id.ms_bottomNavigation);
        mBottomNagivation_2 = (RelativeLayout) stepperLayout.findViewById(R.id.ms_bottomNavigation_type2);

        mProgressBar.setProgressColor(getSelectedColor());
        mProgressBar.setProgressBackgroundColor(getUnselectedColor());

        if (stepperLayout.isInEditMode()) {
            mProgressBar.setVisibility(View.VISIBLE);
            mProgressBar.setProgressCompat(1, false);
            mProgressBar.setMax(3);
        }

        mBottomNagivation_1.setVisibility(View.GONE);
        mBottomNagivation_2.setVisibility(View.VISIBLE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onNewAdapter(@NonNull StepAdapter stepAdapter) {
        super.onNewAdapter(stepAdapter);
        final int stepCount = stepAdapter.getCount();
        mProgressBar.setMax(stepAdapter.getCount());
        mProgressBar.setVisibility(stepCount > 1 ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onStepSelected(int newStepPosition, boolean userTriggeredChange) {
        mProgressBar.setProgressCompat(newStepPosition + 1, userTriggeredChange);
    }
}
