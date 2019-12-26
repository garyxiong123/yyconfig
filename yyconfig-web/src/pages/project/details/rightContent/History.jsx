import React, { Fragment } from 'react';
import { connect } from 'dva';
import { Table } from 'antd';
import styles from '../../index.less';

class History extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      list: [{}, {}]
    };
  }
  componentDidMount() {
    this.onFetchHistoryList();
  }

  onFetchHistoryList = () => {
    const { item, dispatch } = this.props;
    let baseInfo = item.baseInfo || {};
    dispatch({
      type: 'project/commitFind',
      payload: {
        appEnvClusterNamespaceId: baseInfo.id
      }
    })

  }
  renderTable() {
    const columns = [
      {
        title: 'Type',
        dataIndex: 'type',
      },
      {
        title: 'Key',
        dataIndex: 'key',
      },
      {
        title: 'Old Value',
        dataIndex: 'oldValue',
      },
      {
        title: 'New Value',
        dataIndex: 'newValue',
      },
      {
        title: 'Comment',
        dataIndex: 'comment',
      },
    ];
    return (
      <Table
        columns={columns}
        dataSource={[{}]}
        title={() => "2019-12-18 14:28:15"}
        bordered
        pagination={false}
        rowKey={record => {
          return record.id;
        }}
      />
    )
  }
  render() {
    return (
      <Fragment>
        {
          [{}, {}].map(() => (
            <div className={styles.marginBottom25}>
              {this.renderTable()}
            </div>
          ))
        }
      </Fragment>
    );
  }
}

export default connect(({ project, loading }) => ({
  commitFind: project.commitFind,
  loading: loading.effects["project/commitFind"]
}))(History);
