import React, {PureComponent} from 'react';
import PropTypes from 'prop-types';
import {Sidebar, Segment, Button, Menu, Image, Icon, Header} from 'semantic-ui-react';
import SignInModal from './SignInModal';
import SignUpModal from './SignUpModal';
import TreeMapContainer from './TreeMapContainer';

class IconMenu extends PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      openSidebar: false,
      showSignIn: false,
      showSignUp: false
    };
  }

  componentWillReceiveProps(nextProps) {
    if (this.props.show !== nextProps.show) {
      this.setState({openSidebar: nextProps.show});
    }
  }

  toggleVisibility = () => this.setState({visible: !this.state.visible});
  handleSignIn = () => this.setState({showSignIn: !this.state.showSignIn});
  handleSignUp = () => this.setState({showSignUp: !this.state.showSignUp});
  handleLogOut = () => localStorage.clear();

  render() {
    return (
      <div>
        <Sidebar.Pushable as={Segment}>
          <Sidebar as={Menu} animation='push' width='thin' visible={this.state.openSidebar} icon='labeled' size='tiny' vertical>
            {localStorage.getItem('username') == null ? (
              <div>
              <Menu.Item link name='signin'>
                <Button basic color='black' name='signin' onClick={this.handleSignIn}>
                  Sign In
                </Button>
              </Menu.Item>
              <Menu.Item link name='signup'>
                <Button basic color='black' name='signup' onClick={this.handleSignUp}>
                  Sign Up
                </Button>
              </Menu.Item>
              </div>
            ) : null}
            {localStorage.getItem('username') !== null ? (
              <Menu.Item link name='logout'>
                <Button basic color='black' name='logout' onClick={this.handleLogOut}>
                  Log Out
                </Button>
              </Menu.Item>
            ) : null}
          </Sidebar>
          <Sidebar.Pusher>
            <Segment basic>
              <TreeMapContainer/>
            </Segment>
          </Sidebar.Pusher>
        </Sidebar.Pushable>

        <SignInModal show={this.state.showSignIn}/>
        <SignUpModal show={this.state.showSignUp}/>
      </div>
    )
  }
}

IconMenu.propTypes = {
  show: PropTypes.bool.isRequired
}

export default IconMenu;