import React from 'react';
import { connect } from 'dva';
import { Modal, Form, Input, Checkbox, message } from 'antd';
import { project, cluster } from '@/services/project';

const FormItem = Form.Item;
const formItemLayout = {
  labelCol: {
    xs: { span: 24 },
    sm: { span: 6 },
  },
  wrapperCol: {
    xs: { span: 24 },
    sm: { span: 16 },
  },
};

class ClusterAdd extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      loading: false
    };
  }
  componentDidMount() { }

  onSubmit = (e) => {
    const { onCancel } = this.props;
    e.preventDefault();
    this.props.form.validateFieldsAndScroll((err, values) => {
      if (!err) {
        this.setState({
          loading: true
        })
        this.onAddCluster(values);
        onCancel();
      }
    })
  }
  //添加集群
  onAddCluster = async (values) => {
    const { onSave, appDetail } = this.props;
    let env = values.env.join(',');
    let res = await cluster.clusterAdd({ ...values, env: env, appId: appDetail.id });
    if (res && res.code === '1') {
      message.success('添加成功');
      onSave()
    }
    this.setState({
      loading: false
    })
  }

  renderForm() {
    const { getFieldDecorator, getFieldValue } = this.props.form;
    const { envList, appDetail } = this.props;
    return (
      <Form onSubmit={this.onSubmit} {...formItemLayout}>
        <FormItem label="项目Code">
          {getFieldDecorator('appId', {
            initialValue: appDetail.appCode,
            rules: [
              { required: true, message: '请输入项目Code' }
            ]
          })(
            <Input placeholder="请输入项目Code" disabled />
          )}
        </FormItem>
        <FormItem label="集群名称">
          {getFieldDecorator('clusterName', {
            // initialValue: '',
            rules: [
              { required: true, message: '请输入集群名称' }
            ]
          })(
            <Input placeholder="请输入集群名称" />
          )}
        </FormItem>
        <FormItem label="选择环境">
          {getFieldDecorator('env', {
            rules: [
              { required: true, message: "至少选择一个环境" }
            ]
          })(
            <Checkbox.Group>
              {
                envList && envList.map((item, i) => (
                  <Checkbox value={item.env} key={item.env}>{item.env}</Checkbox>
                ))
              }
            </Checkbox.Group>
          )}
        </FormItem>
      </Form>
    )
  }
  render() {
    const { onCancel } = this.props;
    const { loading } = this.state;
    return (
      <Modal
        title={"添加集群"}
        visible={true}
        onCancel={onCancel}
        onOk={this.onSubmit}
        confirmLoading={loading}
      >
        {this.renderForm()}
      </Modal>
    );
  }
}

export default Form.create()(connect(({ project }) => ({
  envList: project.envList,
  appDetail: project.appDetail,
}))(ClusterAdd));
