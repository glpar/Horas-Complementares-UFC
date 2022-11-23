import 'package:exemplo/question.dart';
import 'package:flutter/material.dart';

class Quiz extends StatelessWidget {
  final Question q;
  final List<Widget> buttonsAnswers;

  Quiz(this.q, this.buttonsAnswers);

  @override
  Widget build(BuildContext context) {
    return Column(
      children: <Widget>[
        q,
        ...buttonsAnswers,
      ],
    );
  }
}
