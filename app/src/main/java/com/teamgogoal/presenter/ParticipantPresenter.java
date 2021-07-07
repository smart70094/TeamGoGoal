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

    public void getParticipant(int targetId) {
        Observable<ParticipantDto> apiObservable = participantModel.getParticipant(targetId);

        apiObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((participantDto -> participantView.initialParticipantAdapter(participantDto)), new HttpExceptionAction(this));
    }

    public void inviteParticipant(ParticipantDto.Participant participant) {
        Observable<Void> apiObservable = participantModel.inviteParticipant(participant);

        apiObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((object)-> participantView.inviteParticipantSuccess(), new HttpExceptionAction(this));
    }

    public void deleteParticipant(ParticipantDto.Participant participant) {
        Observable<Void> apiObservable = participantModel.deleteParticipant(participant);

        apiObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }
}
