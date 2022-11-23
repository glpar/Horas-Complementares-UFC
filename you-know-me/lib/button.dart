import 'package:flutter/material.dart';

class MyButton extends StatelessWidget {
  final String text;

  final Function() callBack;

  MyButton(this.text, this.callBack);

  @override
  Widget build(BuildContext context) {
    return Container(
      width: double.infinity,
      child: RaisedButton(
        textColor: Colors.white,
        color: Colors.blue,
        child: Text(text),
        onPressed: callBack,
      ),
    );
  }
}
