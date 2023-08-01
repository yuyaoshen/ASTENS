# ASTENS

### 	-- AST-based ENcoded Sequence

### 	-- 基于抽象语法树的编码序列代码表示方法

​		一种简单的源代码表示方法。首先，使用抽象语法树表示源代码信息；再通过遍历的方式将抽象语法树转换为节点序列；接着，根据预先定义的编码规则，将节点关系和节点类型表示为数值型的结构编码和类型编码；最终实现源代码文本到编码节点序列的转换。

​		该方法是一种将源代码文本处理为特定中间表示的代码转换方法，可应用于多种不同的下游任务，如代码搜索、代码分类、代码克隆检测、代码推荐等。其主要思路是通过抽象语法树表示代码语法结构信息，并采用序列表达的形式以期望简化后续的信息处理方法。例如，在代码搜索任务中，通过该方法转换源代码后，代码搜索问题变为了一个序列匹配问题，而在代码分类任务中，则变为了一个序列数据分类问题。

​		方法的主要流程如下：首先，使用解析器从源代码中解析得到原始的抽象语法树；再使用精简器将原始抽象语法树中的多余信息去除，仅保留节点的层级信息和类型信息；最后，根据遍历顺序依次遍历抽象语法树节点并转换为一个数值型的编码节点序列。

![](/Users/yys/Library/CloudStorage/OneDrive-个人/Documents/Ph.D/Papers/学位论文/figures/代码表示/ASTENS.svg)

​		示例如下图：每个节点都将被表示为节点层级和节点类型两个部分，通过前序遍历将其转换为一个抽象语法树的节点序列，根据编码定义，将节点的层级关系编码为该节点结构编码，节点类型编码为类型编码。

![](/Users/yys/Library/CloudStorage/OneDrive-个人/Documents/Ph.D/Papers/学位论文/figures/代码表示/ASTENS-example.svg)

#### 相关文献：

1. [ASTENS-BWA: Searching partial syntactic similar regions between source code fragments via AST-based encoded sequence alignment](https://www.sciencedirect.com/science/article/abs/pii/S0167642322000727)
2. [ASPDup: AST-Sequence-based Progressive Duplicate Code Detection Tool for Onsite Programming Code](https://dl.acm.org/doi/10.1145/3457913.3457938)

### 源代码 Source Code

该项目为Java项目，可执行Jar文件位于“executable”文件夹中。

该项目提供两种功能：

1. 基于抽象语法树的编码序列代码转换（astensEncoder）：使用ANTLR工具构造不同编程语言的解析器，语法规则文件位于“antlrparsers”文件夹内，目前仅支持C/C++、Java、python3、JavaScript、golang。“astensEncoderXXX.java”实现了不同编程语言XXX的源代码编码序列转换，具体实现过程中已将编码器和精简器进行了合并，遍历节点的同时直接根据所需信息进行编码转换，以提高执行效率。
2. 源代码注释清除及隐名化处理（codeNormalizer）：源代码文本化处理工具，与astensEncoder无关联。通过正则匹配清除源代码中的所有注释信息（cleanComments.java），检测源代码中的函数名和变量名，并将用户命名隐名化（codeNormalizerXXX.java），以屏蔽自然语言信息的干扰。“keywordsList.java”中列举了不同编程语言中的关键字和通用命名，列表中的命名和关键字将不会被隐匿。该工具服务于各类面向源代码文本的处理方法，目前仅支持C/C++。
