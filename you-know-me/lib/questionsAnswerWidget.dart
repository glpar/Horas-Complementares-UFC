import 'package:exemplo/question.dart';
import 'package:flutter/material.dart';
import 'package:exemplo/quiz.dart';
import 'package:exemplo/question.dart';
import 'package:exemplo/buttonAnswer.dart';

class QuestionsAnswerState extends State<QuestionsAnswer> {
  int _cont = 0;
  List<Map<String, Object>> _questions;

  QuestionsAnswerState(this._questions);

  void _incrementCounter(BuildContext context) {
    setState(() {
      _cont++;
      getAnswerSelected() ? null : Navigator.pop(context);
    });
  }

  bool getAnswerSelected() {
    return _cont < _questions.length;
  }

  @override
  Widget build(BuildContext context) {
    List<Map<String, Object>> answersList;
    getAnswerSelected()
        ? answersList = _questions[_cont].cast()['itens']
        : answersList = [];

    List<Widget> buttonsAnswers = answersList
        .map((resp) => MyButtonAnswer(
            resp['text'].toString(), int.parse(resp['score'].toString())))
        .toList();

    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(title: Text('you know me?')),
        body: getAnswerSelected()
            ? Quiz(
                Question(_questions[_cont]['question'].toString()),
                buttonsAnswers,
              )
            : null,
        floatingActionButton: FloatingActionButton(
          onPressed: () => _incrementCounter(context),
          tooltip: 'Increment',
          child: const Icon(Icons.forward),
        ),
      ),
    );
  }
}

class QuestionsAnswer extends StatefulWidget {
  List<Map<String, Object>> data;

  QuestionsAnswer(this.data);

  @override
  QuestionsAnswerState createState() {
    return QuestionsAnswerState(data);
  }
}
