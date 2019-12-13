import React from 'react';
import { connect } from 'dva';
import { Redirect } from 'umi';
import { stringify } from 'querystring';
import PageLoading from '@/components/PageLoading';

class SecurityLayout extends React.Component {
  state = {
    isReady: false,
    isLogin: false
  };

  componentDidMount() {
    let currentUser = JSON.parse(localStorage.getItem('YYuser'));
    this.setState({
      isReady: true,
      isLogin: currentUser && currentUser.id ? true: false
    });
    // const { dispatch } = this.props;

    // if (dispatch) {
    //   dispatch({
    //     type: 'user/fetchCurrent',
    //   });
    // }
  }

  render() {
    const { isReady, isLogin } = this.state;
    const { children, loading } = this.props; // You can replace it to your authentication rule (such as check token exists)
    // 你可以把它替换成你自己的登录认证规则（比如判断 token 是否存在）
    // let currentUser = JSON.parse(localStorage.getItem('user'));
    // const isLogin = currentUser && currentUser.userId;
    // const queryString = stringify({
    //   redirect: window.location.href,
    // });

    if ((!isLogin && loading) || !isReady) {
      return <PageLoading />;
    }

    if (!isLogin) {
      return <Redirect to={`/user/login`}></Redirect>;
    }

    return children;
  }
}

export default connect(({ user, loading }) => ({
  // currentUser: user.currentUser,
  loading: loading.models.user,
}))(SecurityLayout);
