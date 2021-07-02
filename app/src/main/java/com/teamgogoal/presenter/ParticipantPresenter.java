package com.teamgogoal.presenter;

import com.teamgogoal.dto.ParticipantDto;
import com.teamgogoal.model.ParticipantModel;
import com.teamgogoal.service.action.HttpExceptionAction;
import com.teamgogoal.view.interfaces.ParticipantView;

import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ParticipantPresenter extends BasePresenter {

    private ParticipantView participantView;

    private ParticipantModel participantModel;

    public ParticipantPresenter(ParticipantView participantView) {
        this.participantView = participantView;
        this.participantModel = new ParticipantModel();
    }

    @Override
    public void showMessage(String message) {
        participantView.showMessage(message);
    }

    @Override
    public void dismissProgressDialog() {
        participantView.dismissProgressDialog();
    }

    public void getParticipant() {
        Observable<List<ParticipantDto>> apiObservable = participantModel.getParticipant();

        apiObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((participantDtoList -> participantView.initialParticipantAdapter(participantDtoList)), new HttpExceptionAction(this));
    }
}
