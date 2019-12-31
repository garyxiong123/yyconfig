import React from 'react';
import { connect } from 'dva';
import { Modal, Form, message, Steps } from 'antd';

class SyncConfig extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      loading: false,
    };
  }
  onSubmit = () => {

  }

  renderEnv() {
    return (
      <div>
        选择环境
      </div>
    )
  }
  renderTable() {
    return (
      <div>
        选择配置
      </div>
    )
  }
  render() {
    const { onCancel } = this.props;
    const { loading } = this.state;
    return (
      <Modal
        title={`同步配置`}
        visible={true}
        onCancel={onCancel}
        onOk={this.onSubmit}
        confirmLoading={loading}
        width={1000}
      >
        {this.renderEnv()}
      </Modal>
    )
  }
}
export default Form.create()(connect(({ project }) => ({

}))(SyncConfig));