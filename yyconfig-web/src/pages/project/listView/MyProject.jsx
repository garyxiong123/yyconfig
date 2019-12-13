import React from 'react';
import { Card, Row, Col, Icon, Button } from 'antd';
import router from 'umi/router';
import styles from '../index.less';

class MyProject extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
    };
  }
  componentDidMount() { }

  onRouteTo = (pathname, data) => {
    router.push({
      pathname,
      data
    })
  }
  render() {
    return (
      <Row gutter={48}>
        <Col span={8}>
          <Button type="dashed" className={styles.listCard} onClick={()=>this.onRouteTo('/project-create')}>
            <Icon type="plus" />
            <span>新增项目</span>
          </Button>
        </Col>
        {
          [{}, {}, {}, {}].map((item, i) => (
            <Col span={8} key={i}>
              <Card className={styles.listCard} onClick={()=>this.onRouteTo('/project-details')}>
                <h2>名称</h2>
                <p>描述</p>
              </Card>
            </Col>
          ))
        }
      </Row>
    );
  }
}
export default MyProject;
