import React, { Fragment } from 'react';
import { connect } from 'dva';
import { Card, Row, Col, Icon, Button } from 'antd';
import router from 'umi/router';
import styles from '../index.less';
import CreateProject from '../create/';

class MyProject extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      showCreate: false
    };
  }
  // ----------------------------------生命周期-----------------------------
  componentDidMount() {
    this.onFetchlist();
  }
  // ----------------------------------事件-----------------------------
  onRouteTo = (pathname, data) => {
    router.push({
      pathname,
      data
    })
  }
  onFetchlist = () => {
    const { dispatch } = this.props;
    dispatch({
      type: 'project/appList',
      payload: {}
    })
  }
  onShowCreate = () => {
    this.setState({
      showCreate: true
    })
  }
  onCancel = () => {
    this.setState({
      showCreate: false
    })
  }
  onSave=()=>{
    this.onFetchlist();
  }
  // ----------------------------------渲染-----------------------------
  render() {
    const { list } = this.props;
    const { showCreate } = this.state;
    return (
      <Fragment>
        <Row gutter={48}>
          <Col span={8}>
            <Button type="dashed" className={styles.listCard} onClick={this.onShowCreate}>
              <Icon type="plus" />
              <span>新增项目</span>
            </Button>
          </Col>
          {
            list.rows && list.rows.map((item, i) => (
              <Col span={8} key={i}>
                <Card className={styles.listCard} onClick={() => { this.onRouteTo('/project-details') }}>
                  <h2>{item.appCode}</h2>
                  <p>{item.name}</p>
                </Card>
              </Col>
            ))
          }
        </Row>
        {
          showCreate && <CreateProject onCancel={this.onCancel} onSave={this.onSave}/>
        }
      </Fragment>
    );
  }
}

export default connect(({ project, loading }) => ({
  list: project.appList,
  loading: loading.effects["project/appList"]
}))(MyProject);
