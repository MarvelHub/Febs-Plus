<template>
  <a-modal title="新增字典" :visible="dictAddVisiable" :closable="false"
           @ok="handleSubmit" @cancel="onClose">
    <a-form :form="form">
      <a-form-item label='键' v-bind="formItemLayout">
        <a-input-number style="width: 100%"
                        v-model="dict.keyy"
                        v-decorator="['keyy',
                   {rules: [
                    { required: true, message: '键不能为空'}
                  ]}]"/>
      </a-form-item>
      <a-form-item label='值' v-bind="formItemLayout">
        <a-input v-model="dict.valuee"
                 v-decorator="['valuee',
                   {rules: [
                    { required: true, message: '值不能为空'},
                    { max: 20, message: '长度不能超过20个字符'}
                  ]}]"/>
      </a-form-item>
      <a-form-item label='表名' v-bind="formItemLayout">
        <a-input v-model="dict.tableName"
                 v-decorator="['tableName',
                   {rules: [
                    { required: true, message: '表名不能为空'},
                    { max: 20, message: '长度不能超过20个字符'}
                  ]}]"/>
      </a-form-item>
      <a-form-item label='字段' v-bind="formItemLayout">
        <a-input v-model="dict.fieldName"
                 v-decorator="['fieldName',
                   {rules: [
                    { required: true, message: '字段不能为空'},
                    { max: 20, message: '长度不能超过20个字符'}
                  ]}]"/>
      </a-form-item>
    </a-form>
  </a-modal>
</template>
<script>
const formItemLayout = {
  labelCol: { span: 3 },
  wrapperCol: { span: 18 }
};
export default {
  name: 'DictAdd',
  props: {
    dictAddVisiable: {
      default: false
    }
  },
  data () {
    return {
      loading: false,
      formItemLayout,
      form: this.$form.createForm(this),
      dict: {}
    }
  },
  methods: {
    reset () {
      this.loading = false;
      this.dict = {};
      this.form.resetFields()
    },
    onClose () {
      this.reset();
      this.$emit('close')
    },
    handleSubmit () {
      this.form.validateFields((err, values) => {
        if (!err) {
          this.$post('dict', {
            ...this.dict
          }).then(() => {
            this.reset();
            this.$emit('success')
          }).catch(() => {
            this.loading = false
          })
        }
      })
    }
  }
}
</script>
