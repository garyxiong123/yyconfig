import React, { Fragment } from 'react';
import { Card, Table, Radio } from 'antd';

class Case extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
    };
  }
  componentDidMount() { }

  renderTable() {
    const columns = [
      {
        title: 'App ID',
        dataIndex: 'appId',
      },
      {
        title: 'Cluster Name',
        dataIndex: 'clusterName',
      },
      {
        title: 'Data Center',
        dataIndex: 'dataCenter',
      },
      {
        title: 'IP',
        dataIndex: 'ip',
      },
      {
        title: '配置获取时间',
        dataIndex: 'time',
      },
    ];
    return (
      <Table
        columns={columns}
        dataSource={[{}]}
        bordered
        pagination={false}
        rowKey={record => {
          return record.id;
        }}
      />
    )
  }
  renderExtra() {
    return (
      <Radio.Group defaultValue="a" buttonStyle="solid" size="small">
        <Radio.Button value="a">使用最新配置的实例</Radio.Button>
        <Radio.Button value="b">使用非最新配置的实例</Radio.Button>
        <Radio.Button value="c">所有实例</Radio.Button>
      </Radio.Group>
    )
  }
  render() {
    return (
      <Fragment>
        <Card
          title={
            <span style={{ fontSize: 14 }}>实例说明:只展示最近一天访问过Apollo的实例</span>
          }
          bordered={false}
          size="samll"
          extra={this.renderExtra()}
          headStyle={{ backgroundColor: '#f5f5f5' }}
          
        >
          {this.renderTable()}
        </Card>
      </Fragment>
    );
  }
}
export default Case;
