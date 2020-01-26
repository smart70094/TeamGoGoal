package com.teamgogoal.validate;

import android.content.Context;

import com.teamgogoal.dto.TaskDto;
import com.teamgogoal.utils.ToastUtils;

import java.util.List;

public class TggValidator extends Validator{

    public static void  execute(Context context, Object object) {
        List<String> errors = validate(object);

        if(errors.size() > 0) {
            ToastUtils.showShortMessage(context, errors.get(0));
        }
    }
}
