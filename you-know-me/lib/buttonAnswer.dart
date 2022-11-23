import 'package:flutter/material.dart';

class MyButtonAnswer extends StatelessWidget {
  final String text;
  final int correct;

  void blank() {}

  MyButtonAnswer(this.text, this.correct);

  @override
  Widget build(BuildContext context) {
    return Container(
      width: double.infinity,
      child: correct == 1
          ? RaisedButton(
              textColor: Colors.white,
              color: Colors.green,
              child: Text(text),
              onPressed: blank,
            )
          : RaisedButton(
              textColor: Colors.white,
              color: Colors.blue,
              child: Text(text),
              onPressed: blank,
            ),
    );
  }
}
