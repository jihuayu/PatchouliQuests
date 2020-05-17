var ASMAPI = Java.type('net.minecraftforge.coremod.api.ASMAPI');
var Opcodes = Java.type('org.objectweb.asm.Opcodes');
var Label = Java.type('org.objectweb.asm.Label');

function initializeCoreMod() {
    return {
        'PlayerTabTransformer': {
            'target': {
                'type': 'CLASS',
                'name': 'vazkii/patchouli/client/book/gui/GuiBook'
            },
            'transformer': function (cn) {
                var i = null;
                cn.methods.forEach(function (mn) {
                    if (mn.name === 'mouseScrolled') {
                        i = mn;
                    }
                });
                if(i)
                    cn.methods.remove(i);
                var methodVisitor = cn.visitMethod(Opcodes.ACC_PUBLIC, "mouseScrolled", "(DDD)Z", null, null);
                methodVisitor.visitCode();
                var label0 = new Label();
                methodVisitor.visitLabel(label0);
                methodVisitor.visitLineNumber(594, label0);
                methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
                methodVisitor.visitVarInsn(Opcodes.DLOAD, 1);
                methodVisitor.visitVarInsn(Opcodes.DLOAD, 3);
                methodVisitor.visitVarInsn(Opcodes.DLOAD, 5);
                methodVisitor.visitMethodInsn(Opcodes.INVOKESTATIC, "vazkii/patchouli/client/book/gui/AsmUtil", "asm1", "(Lvazkii/patchouli/client/book/gui/GuiBook;DDD)Z", false);
                var label1 = new Label();
                methodVisitor.visitJumpInsn(Opcodes.IFEQ, label1);
                methodVisitor.visitInsn(Opcodes.ICONST_1);
                methodVisitor.visitInsn(Opcodes.IRETURN);
                methodVisitor.visitLabel(label1);
                methodVisitor.visitLineNumber(595, label1);
                methodVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
                methodVisitor.visitVarInsn(Opcodes.DLOAD, 5);
                methodVisitor.visitInsn(Opcodes.DCONST_0);
                methodVisitor.visitInsn(Opcodes.DCMPG);
                var label2 = new Label();
                methodVisitor.visitJumpInsn(Opcodes.IFGE, label2);
                var label3 = new Label();
                methodVisitor.visitLabel(label3);
                methodVisitor.visitLineNumber(596, label3);
                methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
                methodVisitor.visitInsn(Opcodes.ICONST_0);
                methodVisitor.visitInsn(Opcodes.ICONST_1);
                methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "vazkii/patchouli/client/book/gui/GuiBook", "changePage", "(ZZ)V", false);
                var label4 = new Label();
                methodVisitor.visitJumpInsn(Opcodes.GOTO, label4);
                methodVisitor.visitLabel(label2);
                methodVisitor.visitLineNumber(597, label2);
                methodVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
                methodVisitor.visitVarInsn(Opcodes.DLOAD, 5);
                methodVisitor.visitInsn(Opcodes.DCONST_0);
                methodVisitor.visitInsn(Opcodes.DCMPL);
                methodVisitor.visitJumpInsn(Opcodes.IFLE, label4);
                var label5 = new Label();
                methodVisitor.visitLabel(label5);
                methodVisitor.visitLineNumber(598, label5);
                methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
                methodVisitor.visitInsn(Opcodes.ICONST_1);
                methodVisitor.visitInsn(Opcodes.ICONST_1);
                methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "vazkii/patchouli/client/book/gui/GuiBook", "changePage", "(ZZ)V", false);
                methodVisitor.visitLabel(label4);
                methodVisitor.visitLineNumber(600, label4);
                methodVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
                methodVisitor.visitInsn(Opcodes.ICONST_1);
                methodVisitor.visitInsn(Opcodes.IRETURN);
                var label6 = new Label();
                methodVisitor.visitLabel(label6);
                methodVisitor.visitLocalVariable("this", "Lvazkii/patchouli/client/book/gui/GuiBook;", null, label0, label6, 0);
                methodVisitor.visitLocalVariable("mouseX", "D", null, label0, label6, 1);
                methodVisitor.visitLocalVariable("mouseY", "D", null, label0, label6, 3);
                methodVisitor.visitLocalVariable("scroll", "D", null, label0, label6, 5);
                methodVisitor.visitMaxs(7, 7);
                methodVisitor.visitEnd();
                return cn;
            }
        }
    };
}