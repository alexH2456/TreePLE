import React, {PureComponent} from 'react';
import PropTypes from 'prop-types';
import {Sidebar, Segment, Button, Menu} from 'semantic-ui-react';
import TreeMap from './TreeMap';
import SignInModal from './SignInModal';
import SignUpModal from './SignUpModal';
import MyTreesModal from './MyTreesModal';
import MyForecastsModal from './MyForecastsModal';
import CreateSpeciesModal from './CreateSpeciesModal';
import CreateMunicipalityModal from './CreateMunicipalityModal';
import HelpModal from './HelpModal';
import {authenticated} from './Requests';

class SideBar extends PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      user: localStorage.getItem('username'),
      scientist: '',
      showMenu: false,
      showSignIn: false,
      showSignUp: false,
      showMyTrees: false,
      showMyForecasts: false,
      showCreateSpecies: false,
      showCreateMunicipality: false,
      showHelp: false,
      refreshMap: false,
      error: ''
    };
  }

  componentWillMount() {
    this.authenticatedUser(localStorage.getItem('username'));
  }

  componentWillReceiveProps(nextProps) {
    if (this.props.show !== nextProps.show) {
      this.setState({showMenu: nextProps.show});
    }
  }

  componentDidUpdate(nextProps, nextState) {
    if (nextState.refreshMap === true) {
      this.setState({refreshMap: false});
    }
  }

  authenticatedUser = (username) => {
    if (username) {
      const user = {username: username};
      authenticated(user)
        .then(({data}) => {
          this.setState({scientist: data.authenticated});
        })
        .catch(({response: {data}}) => {
          this.setState({
            error: data.message,
            scientist: false
          });
        });
    } else {
      this.setState({scientist: false});
    }
  }

  toggleSignIn = () => {
    const user = localStorage.getItem('username');
    this.authenticatedUser(user);
    this.setState((prevState) => ({showSignIn: !prevState.showSignIn, user: user}));
  }
  toggleSignUp = () => {
    const user = localStorage.getItem('username');
    this.authenticatedUser(user);
    this.setState((prevState) => ({showSignUp: !prevState.showSignUp, user: user}));
  }
  toggleRegister = () => this.setState((prevState) => ({showSignIn: !prevState.showSignIn, showSignUp: !prevState.showSignUp}));

  toggleMyTrees = (refreshMap) => this.setState((prevState) => ({showMyTrees: !prevState.showMyTrees, refreshMap: refreshMap}));

  toggleMyForecasts = () => this.setState((prevState) => ({showMyForecasts: !prevState.showMyForecasts}));

  toggleCreateSpecies = () => this.setState((prevState) => ({showCreateSpecies: !prevState.showCreateSpecies}));

  toggleCreateMunicipality = (refreshMap) => this.setState((prevState) => ({showCreateMunicipality: !prevState.showCreateMunicipality, refreshMap: refreshMap}));

  toggleHelp = () => this.setState((prevState) => ({showHelp: !prevState.showHelp}));

  onLogOut = () => {
    localStorage.clear();
    this.setState({
      user: localStorage.getItem('username'),
      scientist: false
    });
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
                <Menu.Item link name='mytrees'>
                  <Button basic fluid color='black' name='mytrees' onClick={() => this.toggleMyTrees(false)}>
                    Trees
                  </Button>
                </Menu.Item>
                <Menu.Item link name='myforecasts'>
                  <Button basic fluid color='black' name='myforecasts' onClick={this.toggleMyForecasts}>
                    Forecasts
                  </Button>
                </Menu.Item>
                {this.state.scientist ? (
                  <Menu.Item link name='createspecies'>
                    <Button basic fluid color='black' name='createspecies' onClick={this.toggleCreateSpecies}>
                      Species
                    </Button>
                  </Menu.Item>
                ) : null}
                {this.state.scientist ? (
                  <Menu.Item link name='createmunicipality'>
                    <Button basic fluid color='black' name='createmunicipality' onClick={() => this.toggleCreateMunicipality(false)}>
                      Municipalities
                    </Button>
                  </Menu.Item>
                ) : null}
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
              <TreeMap refreshMap={this.state.refreshMap} onSustainabilityChange={this.props.onSustainabilityChange}/>
            </Segment>
          </Sidebar.Pusher>
        </Sidebar.Pushable>

        {this.state.showSignIn ? (
          <SignInModal onClose={this.toggleSignIn} onRegister={this.toggleRegister}/>
        ) : null}
        {this.state.showSignUp ? (
          <SignUpModal onClose={this.toggleSignUp} onRegister={this.toggleRegister}/>
        ) : null}
        {this.state.showMyTrees ? (
          <MyTreesModal onClose={this.toggleMyTrees}/>
        ) : null}
        {this.state.showMyForecasts ? (
          <MyForecastsModal onClose={this.toggleMyForecasts}/>
        ) : null}
        {this.state.showCreateSpecies ? (
          <CreateSpeciesModal onClose={this.toggleCreateSpecies}/>
        ) : null}
        {this.state.showCreateMunicipality ? (
          <CreateMunicipalityModal onClose={this.toggleCreateMunicipality}/>
        ) : null}
        {this.state.showHelp ? (
          <HelpModal onClose={this.toggleHelp}/>
        ) : null}
      </div>
    );
  }
}

SideBar.propTypes = {
  show: PropTypes.bool.isRequired,
  onSustainabilityChange: PropTypes.func.isRequired
};

export default SideBar;
