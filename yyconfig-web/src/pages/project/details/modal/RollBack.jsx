import React from 'react';
import { connect } from 'dva';
import { Modal, Form, Input, Checkbox, message, Table } from 'antd';
import { project } from '@/services/project';
import { rollBackStatus } from '@/pages/contants/'

class RollBackModal extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      loading: false,
    };
  }
  componentDidMount() {
    this.onFetchReleasesCompare();
  }

  onSubmit = () => {
    const { releasesActiveInfo } = this.props;
    let releaseId = releasesActiveInfo[0] ? releasesActiveInfo[0].id : '';
    if (releaseId) {
      this.onRollBack(releaseId)
    }
  }
  onRollBack = async (releaseId) => {
    const { onCancel, onSave } = this.props;
    let res = await project.rollBack({ releaseId });
    if (res && res.code === '1') {
      message.success('回滚成功');
      onSave();
      onCancel();
    }
    this.setState({
      loading: false
    })
  }
  onFetchReleasesCompare = () => {
    const { dispatch, releasesActiveInfo } = this.props;
    let baseReleaseId = releasesActiveInfo[0] ? releasesActiveInfo[0].id : '',
      toCompareReleaseId = releasesActiveInfo[1] ? releasesActiveInfo[1].id : '';

    if (baseReleaseId && toCompareReleaseId) {
      dispatch({
        type: 'project/releasesCompare',
        payload: {
          baseReleaseId,
          toCompareReleaseId
        }
      })
    }
  }

  renderForm() {
    const { releasesCompare } = this.props;
    const columns = [
      {
        title: 'Type',
        dataIndex: 'type',
        render:(text, record)=>(
          <span>{rollBackStatus[text]}</span>
        )
      },
      {
        title: 'Key',
        dataIndex: 'entity.firstEntity.key',
      },
      {
        title: '回滚前',
        dataIndex: 'entity.firstEntity.value',
      },
      {
        title: '回滚后',
        dataIndex: 'entity.secondEntity.value',
      },
    ];
    return (
      <div>
        <Table
          columns={columns}
          dataSource={releasesCompare.changes || []}
          // loading={loading}
          pagination={false}
          rowKey={(record, i) => {
            return i;
          }}
        />
      </div>
    )
  }
  render() {
    const { onCancel, releasesActiveInfo } = this.props;
    const { loading } = this.state;
    return (
      <Modal
        title={`${releasesActiveInfo[0] && releasesActiveInfo[0].name} 回滚到 ${releasesActiveInfo[1] && releasesActiveInfo[1].name}`}
        visible={true}
        onCancel={onCancel}
        onOk={this.onSubmit}
        confirmLoading={loading}
        okText="回滚"
        width={700}
      >
        {this.renderForm()}
      </Modal>
    )
  }
}
export default Form.create()(connect(({ project }) => ({
  releasesActiveInfo: project.releasesActiveInfo,
  releasesCompare: project.releasesCompare
}))(RollBackModal));