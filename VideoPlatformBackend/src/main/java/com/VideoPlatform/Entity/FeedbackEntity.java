package com.VideoPlatform.Entity;

import lombok.Data;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import javax.persistence.*;

//@Entity
//@EntityScan
//@Data
//@Table(name = "realtime_feedback")
public class FeedbackEntity {
    @Id
    @Column(name = "feedback_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "feedback_data_generator")
    @SequenceGenerator(name="feedback_data_generator", sequenceName = "feedback_data_seq",allocationSize = 1)
    private int feedback_id;

    @Column(name = "account_id")
    private int accountId;

    @Column(name = "user_id")
    private int userId;

    @Column(name = "paper_id")
    private int paperId;

    @Column(name = "question_id")
    private int questionId;

    //created on,valid till
}
