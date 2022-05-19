package com.roshan.questionnaire.Models

class AnswerModel() {
    var answerText: String = ""
        get() = field
        set(value) {field = value}
    var explanationText: String = ""
        get() = field
        set(value) {field = value}
    var answerBy: String = ""
        get() = field
        set(value) {field = value}
    var answerId: String = ""
        get() = field
        set(value) {field = value}
    var likeCount: Int = 0
        get() = field
        set(value) {field = value}
    var answerAt: Long = 0
        get() = field
        set(value) {field = value}
}