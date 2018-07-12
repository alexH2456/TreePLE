import React, {PureComponent} from 'react';
import PropTypes from 'prop-types';
import {Sidebar, Segment, Button, Menu} from 'semantic-ui-react';
import SignInModal from './SignInModal';
import SignUpModal from './SignUpModal';
import TreeMap from './TreeMap';
import MyForecastsModal from './MyForecastsModal';
import CreateForecastModal from './CreateForecastModal';
import HelpModal from './HelpModal';

class IconMenu extends PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      user: localStorage.getItem('username'),
      showMenu: false,
      showSignIn: false,
      showSignUp: false,
      showMyForecasts: false,
      showCreateForecast: false,
      showHelp: false
    };
  }

  componentWillReceiveProps(nextProps) {
    if (this.props.show !== nextProps.show) {
      this.setState({showMenu: nextProps.show});
    }
  }

  toggleSignIn = () => this.setState((prevState) => ({showSignIn: !prevState.showSignIn, user: localStorage.getItem('username')}));
  toggleSignUp = () => this.setState((prevState) => ({showSignUp: !prevState.showSignUp, user: localStorage.getItem('username')}));
  toggleRegister = () => this.setState((prevState) => ({showSignIn: !prevState.showSignIn, showSignUp: !prevState.showSignUp}));

  toggleMyForecasts = () => this.setState((prevState) => ({showMyForecasts: !prevState.showMyForecasts}));
  toggleCreateForecast = () => this.setState((prevState) => ({showCreateForecast: !prevState.showCreateForecast}));
  toggleForecast = () => this.setState((prevState) => ({showMyForecasts: !prevState.showMyForecasts, showCreateForecast: !prevState.showCreateForecast}));

  toggleHelp = () => this.setState((prevState) => ({showHelp: !prevState.showHelp}));

  onLogOut = () => {
    localStorage.clear();
    this.setState({user: localStorage.getItem('username')});
  }

  render() {
    return (
      <div>
        <Sidebar.Pushable as={Segment}>
          <Sidebar vertical as={Menu} size='tiny' icon='labeled' animation='push' width='thin' visible={this.state.showMenu}>
            {!this.state.user ? (
              <div>
                <Menu.Item link name='signin'>
                  <Button basic fluid color='black' name='signin' onClick={this.toggleSignIn}>
                    Sign In
                  </Button>
                </Menu.Item>
                <Menu.Item link name='signup'>
                  <Button basic fluid color='black' name='signup' onClick={this.toggleSignUp}>
                    Sign Up
                  </Button>
                </Menu.Item>
              </div>
            ) : (
              <div>
                <Menu.Item link name='myforecasts'>
                  <Button basic fluid color='black' name='myforecasts' onClick={this.toggleMyForecasts}>
                    Forecasts
                  </Button>
                </Menu.Item>
                <Menu.Item link name='logout'>
                  <Button basic fluid color='black' name='logout' onClick={this.onLogOut}>
                    Log Out
                  </Button>
                </Menu.Item>
              </div>
            )}
            <Menu.Item link name='help'>
              <Button basic fluid color='black' name='help' onClick={this.toggleHelp}>
                Help
              </Button>
            </Menu.Item>
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
        {this.state.showMyForecasts ? (
          <MyForecastsModal onClose={this.toggleMyForecasts} onForecast={this.toggleForecast}/>
        ) : null}
        {this.state.showCreateForecast ? (
          <CreateForecastModal onClose={this.toggleCreateForecast} onForecast={this.toggleForecast}/>
        ) : null}
        {this.state.showHelp ? (
          <HelpModal onClose={this.toggleHelp}/>
        ) : null}
      </div>
    );
  }
}

IconMenu.propTypes = {
  show: PropTypes.bool.isRequired,
  onSustainabilityChange: PropTypes.func.isRequired
};

export default IconMenu;
