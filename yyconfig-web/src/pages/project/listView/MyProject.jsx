import React from 'react';
import { Card, Row, Col, Icon, Button } from 'antd';
import styles from '../index.less';

class MyProject extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
    };
  }
  componentDidMount() { }
  render() {
    return (
      <Row gutter={48}>
        <Col span={8}>
          <Button type="dashed" className={styles.listCard}>
            <Icon type="plus" />
            <span>新增项目</span>
          </Button>
        </Col>
        {
          [{}, {}, {}, {}].map(() => (
            <Col span={8}>
              <Card className={styles.listCard}>
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
