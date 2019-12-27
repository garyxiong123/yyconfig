import React, { Fragment } from 'react';
import { connect } from 'dva';
import { Card, Tabs, Row, Col, Empty } from 'antd';
import styles from '../index.less';
import Loading from '@/components/PageLoading/';
import router from 'umi/router';

const { TabPane } = Tabs;

class PublicSpace extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      key: '0'
    };
  }
  componentDidMount() {
    this.onFetchList();
  }

  onTabChange = (key) => {
    this.setState({
      key
    })
  }
  onFetchList = () => {
    const { dispatch } = this.props;
    dispatch({
      type: 'system/openNamespaceType',
      payload: {}
    })
  }
  onRouteTo = (vo) => {
    let app = vo.app || {};
    router.push({
      pathname: `/project/details/${app.id}`,
      query: {
        appId: app.id,
        appCode: app.appCode
      }
    })
  }
  renderItem(item) {
    let appNamespaces = item.appNamespaces || [];
    return (
      <Row gutter={48}>
        {
          appNamespaces.length ?
            <Fragment>
              {
                appNamespaces.map((vo, index) => (
                  <Col lg={6} md={8} sm={24} key={vo.id}>
                    <Card className={styles.listCard} onClick={() => { this.onRouteTo(vo) }}>
                      <h2>{vo.name}</h2>
                      <p>{vo.comment}</p>
                    </Card>
                  </Col>
                ))
              }
            </Fragment> :
            <Fragment>
              <Empty />
            </Fragment>
        }
      </Row>
    )
  }
  render() {
    const { key } = this.state;
    const { list, loading } = this.props;
    return (
      <Fragment>
        {/* {
          loading ?
            <Loading /> :
            <Tabs activeKey={key} onChange={this.onTabChange} type="card">
              {
                list.map((item, i) => (
                  <TabPane tab={item.name} key={i}>
                    {this.renderItem(item)}
                  </TabPane>
                ))
              }
            </Tabs>
        } */}
        {
          list.map((item, i) => (
            <Card title={item.name} key={i} bordered={false}>
              {this.renderItem(item)}
            </Card>
          ))
        }
      </Fragment>

    );
  }
}
export default connect(({ project, system, loading }) => ({
  list: system.openNamespaceType,
  loading: loading.effects["system/openNamespaceType"]
}))(PublicSpace);
