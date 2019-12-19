import React, { Fragment } from 'react';
import { connect } from 'dva';
import { Modal, Form, Radio, Input, Select, Transfer } from 'antd';
import styles from '../../index.less';

const FormItem = Form.Item;
const { Option } = Select;
const { TextArea } = Input;
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

class NamespaceAdd extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      currentType: 'relation',
      namespaceList: [
        {
          id: 1,
          value: 'namespace1'
        },
        {
          id: 2,
          value: 'namespace2'
        }
      ],
      leftNoSelect: [
        {
          id: 1,
          text: '1'
        },
        {
          id: 2,
          text: '2'
        },
        {
          id: 3,
          text: '3'
        },
      ],
      leftKeys: [],
      rightKeys: []
    };
  }
  componentDidMount() { }

  onChange = (e) => {
    this.setState({
      currentType: e.target.value
    })
  }
  onServiceChange=(targetKeys, direction, moveKeys)=>{
    // console.log('targetKeys-->', targetKeys)
    // console.log('direction-->', direction)
    // console.log('moveKeys-->', moveKeys)
    this.setState({ rightKeys: targetKeys })
  }
  onServiceSelectChange=(sourceSelectedKeys, targetSelectedKeys)=>{
    // console.log('sourceSelectedKeys-->', sourceSelectedKeys)
    // console.log('targetSelectedKeys-->', targetSelectedKeys)
    this.setState({ leftKeys: [...sourceSelectedKeys, ...targetSelectedKeys] });
  }

  renderRelation() {
    const { getFieldDecorator, getFieldValue } = this.props.form;
    const { appDetail } = this.props;
    const { namespaceList } = this.state;
    return (
      <Form onSubmit={this.onSubmit} {...formItemLayout}>
        <FormItem label="项目Id">
          {getFieldDecorator('appId', {
            initialValue: appDetail.appCode,
            rules: [
              { required: true, message: '请输入项目Id' }
            ]
          })(
            <Input placeholder="请输入项目Id" disabled />
          )}
        </FormItem>
        <FormItem label="namespace">
          {getFieldDecorator('namespace', {
            // initialValue: undefined,
            rules: [
              { required: true, message: '请选择namespace' }
            ]
          })(
            <Select>
              {
                namespaceList.map((item) => (
                  <Option value={item.id} key={item.id}>{item.value}</Option>
                ))
              }
            </Select>
          )}
        </FormItem>

      </Form>
    )
  }
  renderServiceItem() {
    const { leftNoSelect, leftKeys, rightKeys } = this.state;
    return (
      <Transfer
        dataSource={leftNoSelect}
        titles={['待选', '已选']}
        targetKeys={rightKeys}
        selectedKeys={leftKeys}
        onChange={this.onServiceChange}
        onSelectChange={this.onServiceSelectChange}
        // onScroll={this.handleScroll}
        render={item => item.text}
        rowKey={record => record.id}
      />
    )
  }
  renderCreate() {
    const { getFieldDecorator, getFieldValue } = this.props.form;
    const { appDetail } = this.props;
    let department = appDetail.department || {};
    return (
      <Form onSubmit={this.onSubmit} {...formItemLayout}>
        <FormItem label="项目Id">
          {getFieldDecorator('appId', {
            initialValue: appDetail.appCode,
            rules: [
              { required: true, message: '请输入项目Id' }
            ]
          })(
            <Input placeholder="请输入项目Id" disabled />
          )}
        </FormItem>
        <FormItem label="名称">
          {getFieldDecorator('name', {
            // initialValue: appDetail.appCode,
            rules: [
              { required: true, message: '请输入名称' }
            ]
          })(
            <Input placeholder="请输入名称" addonBefore={`${department.code}.`} />
          )}
        </FormItem>
        <FormItem label="类型">
          {getFieldDecorator('type', {
            initialValue: 'public',
            rules: [
              { required: true, message: '请输入名称' }
            ]
          })(
            <Radio.Group>
              <Radio value="public">public</Radio>
              <Radio value="protect">protect</Radio>
              <Radio value="private">private</Radio>
            </Radio.Group>
          )}
        </FormItem>
        {
          getFieldValue('type') === 'protect' &&
          <Fragment>
            <FormItem label="公有命名空间类型">
              {getFieldDecorator('commonType', {
                // initialValue: undefined,
                rules: [
                  { required: true, message: '请选择公有命名空间类型' }
                ]
              })(
                <Select>
                  <Option value={'DB'}>DB</Option>
                  <Option value={'REDIS'}>REDIS</Option>
                </Select>
              )}
            </FormItem>
            <FormItem label=" " colon={false}>
              {this.renderServiceItem()}
            </FormItem>
          </Fragment>
        }
        <FormItem label="备注">
          {getFieldDecorator('comment', {
            // rules: [
            //   { required: true, message: '请输入备注' }
            // ]
          })(
            <TextArea placeholder="请输入备注" rows={4} />
          )}
        </FormItem>
      </Form>
    )
  }
  render() {
    const { onCancel } = this.props;
    const { currentType } = this.state;
    return (
      <Modal
        title="新建Namespace"
        visible={true}
        onCancel={onCancel}
        width={700}
      >
        <div className={styles.marginBottom25} style={{ textAlign: 'right' }}>
          <Radio.Group value={currentType} buttonStyle="solid" onChange={this.onChange}>
            <Radio.Button value="relation">关联公共Namespace</Radio.Button>
            <Radio.Button value="create">创建Namespace</Radio.Button>
          </Radio.Group>
        </div>
        {
          currentType === 'relation' && this.renderRelation()
        }
        {
          currentType === 'create' && this.renderCreate()
        }
      </Modal>
    );
  }
}

export default Form.create()(connect(({ project }) => ({
  appDetail: project.appDetail
}))(NamespaceAdd));
