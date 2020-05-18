var ASMAPI = Java.type('net.minecraftforge.coremod.api.ASMAPI');
var Opcodes = Java.type('org.objectweb.asm.Opcodes');

function initializeCoreMod() {
    return {
        'PlayerTabTransformer': {
            'target': {
                'type': 'CLASS',
                'name': 'vazkii/patchouli/common/book/BookRegistry'
            },
            'transformer': function (cn) {
                cn.fields.forEach(function (mn) {
                    if (mn.name === 'GSON') {
                        mn.access -= Opcodes.ACC_FINAL;
                    }
                });
                return cn;
            }
        }
    };
}