var ASMAPI = Java.type('net.minecraftforge.coremod.api.ASMAPI');
var Opcodes = Java.type('org.objectweb.asm.Opcodes');

function initializeCoreMod() {
    return {
        'PlayerTabTransformer': {
            'target': {
                'type': 'CLASS',
                'name': 'vazkii/patchouli/common.util/SerializationUtil'
            },
            'transformer': function (cn) {
                //遍历ClassNode下的methods
                cn.fields.forEach(function (mn) {
                    if (mn.name === 'RAW_GSON' || mn.name === 'PRETTY_GSON') {
                        mn.access -= Opcodes.ACC_FINAL;
                    }
                });

                //返回修改后的ClassNode对象
                return cn;
            }
        }
    };
}