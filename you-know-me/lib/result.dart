import 'package:flutter/material.dart';

class Result extends StatelessWidget {
  String text;
  Function() restartQuiz;
  Function() questionsAnswer;

  Result(this.text, this.restartQuiz, this.questionsAnswer);

  @override
  Widget build(BuildContext context) {
    return Column(
      mainAxisAlignment: MainAxisAlignment.center,
      children: <Widget>[
        Container(
          width: double.infinity,
          margin: EdgeInsets.all(10),
          child: Text(
            text,
            style: const TextStyle(fontSize: 28),
            textAlign: TextAlign.center,
          ),
        ),
        FlatButton(
          child: Text(
            'restart',
            style: TextStyle(fontSize: 18),
          ),
          textColor: Colors.blue,
          onPressed: restartQuiz,
        ),
        FlatButton(
          child: Text(
            'questions answer',
            style: TextStyle(fontSize: 18),
          ),
          textColor: Colors.blue,
          onPressed: questionsAnswer,
        )
      ],
    );
  }
}
