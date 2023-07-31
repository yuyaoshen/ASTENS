# Usage 使用手册

## astensEncoder.jar

支持处理的语言：C/C++、Java、python3、JavaScript、golang

请预先创建文件夹“source”，并在"source"文件夹中存放需要解析转换的源代码文件，可一次性将所有需要处理的源代码放入该文件夹中，不限语言。

程序将会自动创建"astens"文件夹，并写入与源代码文件名对应的转换后ASTENS文件。ASTENS文件包含三列，第一列为当前抽象语法树节点的ASTENS编码，第二列为抽象语法树节点类型，第三列为抽象语法树节点所在源代码的对应行号。

命令行操作：

- java -jar astensEncoder.jar	将"source"文件夹中的源代码转换为ASTENS文件并存入"astens"文件夹中
- java -jar astensEncoder-WithDot.jar	将"source"文件夹中的源代码转换为ASTENS文件并存入"astens"文件夹中，同时生成对应文件的抽象语法树图，并以dot文件保存



## codeNormalizer.jar

支持处理的语言：C/C++、Java?

请预先创建文件夹“source”，并在"source"文件夹中存放需要解析转换的源代码文件，可一次性将所有需要处理的源代码放入该文件夹中，不限语言。

程序将会自动创建"normalization"文件夹，并写入与源代码文件名对应的规范化源代码文件。

命令行操作：

- java -jar codeNormalizer.jar	清除"source"文件夹中源代码的所有注释信息，隐名化所有自定义命名的函数名和变量名，并存入"normalization"文件夹中

