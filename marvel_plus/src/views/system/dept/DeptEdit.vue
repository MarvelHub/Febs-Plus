<template>
    <a-modal title="修改部门" :visible="deptEditVisiable" :closable="false" @ok="handleSubmit" @cancel="onClose"
             :width="600">
        <a-form :form="form">
            <a-form-item label='部门名称' v-bind="formItemLayout">
                <a-input v-decorator="['deptName',
                   {rules: [
                    { required: true, message: '部门名称不能为空'},
                    { max: 20, message: '长度不能超过20个字符'}
                  ]}]"/>
            </a-form-item>
            <a-form-item label='部门排序' v-bind="formItemLayout">
                <a-input-number v-decorator="['orderNum']" style="width: 100%"/>
            </a-form-item>
            <a-form-item label='上级部门' v-bind="formItemLayout">
                <div class="inner">
                    <a-tree :key="deptTreeKey" :checkable="true" :checkStrictly="true" @check="handleCheck"
                            class="innerbox" style="height: 200px"
                            @expand="handleExpand" :expandedKeys="expandedKeys" :defaultCheckedKeys="defaultCheckedKeys"
                            :treeData="deptTreeData">
                    </a-tree>
                </div>
            </a-form-item>
        </a-form>
    </a-modal>
</template>
<script>
    const formItemLayout = {
        labelCol: {span: 3},
        wrapperCol: {span: 18}
    };
    export default {
        name: 'DeptEdit',
        props: {
            deptEditVisiable: {
                default: false
            }
        },
        data() {
            return {
                loading: false,
                formItemLayout,
                form: this.$form.createForm(this),
                deptTreeKey: +new Date(),
                dept: {},
                checkedKeys: [],
                expandedKeys: [],
                defaultCheckedKeys: [],
                deptTreeData: []
            }
        },
        methods: {
            reset() {
                this.loading = false;
                this.deptTreeKey = +new Date();
                this.expandedKeys = this.checkedKeys = this.defaultCheckedKeys = [];
                this.button = {};
                this.form.resetFields()
            },
            onClose() {
                this.reset();
                this.$emit('close')
            },
            handleCheck(checkedKeys) {
                this.checkedKeys = checkedKeys
            },
            handleExpand(expandedKeys) {
                this.expandedKeys = expandedKeys
            },
            setFormValues({...dept}) {
                this.form.getFieldDecorator('deptName');
                this.form.setFieldsValue({'deptName': dept.text});
                this.form.getFieldDecorator('orderNum');
                this.form.setFieldsValue({'orderNum': dept.order});
                if (dept.parentId !== '0') {
                    this.defaultCheckedKeys.push(dept.parentId);
                    this.checkedKeys = this.defaultCheckedKeys;
                    this.expandedKeys = this.checkedKeys
                }
                this.dept.deptId = dept.id
            },
            handleSubmit() {
                let checkedArr = Object.is(this.checkedKeys.checked, undefined) ? this.checkedKeys : this.checkedKeys.checked;
                if (checkedArr.length > 1) {
                    this.$message.error('最多只能选择一个上级部门，请修改');
                    return
                }
                if (checkedArr[0] === this.dept.deptId) {
                    this.$message.error('不能选择自己作为上级部门，请修改');
                    return
                }
                this.form.validateFields((err, values) => {
                    if (!err) {
                        this.loading = true;
                        let dept = this.form.getFieldsValue();
                        dept.parentId = checkedArr[0];
                        if (Object.is(dept.parentId, undefined)) {
                            dept.parentId = 0
                        }
                        dept.deptId = this.dept.deptId;
                        this.$put('dept', {
                            ...dept
                        }).then(() => {
                            this.reset();
                            this.$emit('success')
                        }).catch(() => {
                            this.loading = false
                        })
                    }
                })
            }
        },
        watch: {
            deptEditVisiable() {
                if (this.deptEditVisiable) {
                    this.$get('dept').then((r) => {
                        this.deptTreeData = r.data.rows.children;
                        this.deptTreeKey = +new Date()
                    })
                }
            }
        }
    }
</script>
<style lang="less" scoped>
    @import "../../.././static/css/Scrollbar";
</style>
