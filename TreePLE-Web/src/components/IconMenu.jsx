import React, {PureComponent} from 'react';
import PropTypes from 'prop-types';
import {Sidebar, Segment, Button, Menu, Image, Icon, Header} from 'semantic-ui-react';
import SignInModal from './SignInModal';
import SignUpModal from './SignUpModal';
import TreeMap from './TreeMap';

class IconMenu extends PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      showMenu: false,
      showSignIn: false,
      showSignUp: false
    };
  }

  componentWillReceiveProps(nextProps) {
    if (this.props.show !== nextProps.show) {
      this.setState({showMenu: nextProps.show});
    }
  }

  toggleSignIn = () => this.setState(prevState => ({showSignIn: !prevState.showSignIn}));
  toggleSignUp = () => this.setState(prevState => ({showSignUp: !prevState.showSignUp}));
  toggleRegister = () => this.setState(prevState => ({showSignIn: !prevState.showSignIn, showSignUp: !prevState.showSignUp}));

  onLogOut = () => localStorage.clear();

  render() {
    return (
      <div>
        <Sidebar.Pushable as={Segment}>
          <Sidebar as={Menu} animation='push' width='thin' visible={this.state.showMenu} icon='labeled' size='tiny' vertical>
            {localStorage.getItem('username') == null ? (
              <div>
              <Menu.Item link name='signin'>
                <Button basic color='black' name='signin' onClick={this.toggleSignIn}>
                  Sign In
                </Button>
              </Menu.Item>
              <Menu.Item link name='signup'>
                <Button basic color='black' name='signup' onClick={this.toggleSignUp}>
                  Sign Up
                </Button>
              </Menu.Item>
              </div>
            ) : (
              <Menu.Item link name='logout'>
                <Button basic color='black' name='logout' onClick={this.onLogOut}>
                  Log Out
                </Button>
              </Menu.Item>
            )}
          </Sidebar>
          <Sidebar.Pusher>
            <Segment basic>
              <TreeMap onSustainabilityChange={this.props.onSustainabilityChange} random={5}/>
            </Segment>
          </Sidebar.Pusher>
        </Sidebar.Pushable>

        {this.state.showSignIn ? (
          <SignInModal onClose={this.toggleSignIn} onRegister={this.toggleRegister}/>
        ) : null}
        {this.state.showSignUp ? (
          <SignUpModal onClose={this.toggleSignUp} onRegister={this.toggleRegister}/>
        ) : null}
      </div>
    )
  }
}

IconMenu.propTypes = {
  show: PropTypes.bool.isRequired
}

export default IconMenu;