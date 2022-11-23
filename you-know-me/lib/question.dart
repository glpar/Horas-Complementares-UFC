import 'package:flutter/cupertino.dart';
import 'package:flutter/src/widgets/framework.dart';

class Question extends StatelessWidget {
  final String text;

  Question(this.text);

  @override
  Widget build(BuildContext context) {
    return Container(
      width: double.infinity,
      margin: EdgeInsets.all(10),
      child: Text(
        text,
        style: const TextStyle(fontSize: 28),
        textAlign: TextAlign.center,
      ),
    );
  }
}
