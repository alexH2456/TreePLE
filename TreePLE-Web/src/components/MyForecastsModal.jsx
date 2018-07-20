import React, {PureComponent} from 'react';
import PropTypes from 'prop-types';
import {Button, Divider, Grid, Header, Icon, Message, Modal} from 'semantic-ui-react';
import CreateForecastModal from './CreateForecastModal';
import {getUserForecasts, deleteForecast} from './Requests';

class MyForecastsModal extends PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      user: localStorage.getItem('username'),
      forecasts: [],
      createFcModal: false,
      analysisModal: false,
      error: ''
    };
  }

  componentWillMount() {
    this.loadForecasts();
  }

  loadForecasts = () => {
    const user = this.state.user;

    getUserForecasts(user)
      .then(({data}) => this.setState({forecasts: data}))
      .catch(({response: {data}}) => this.setState({error: data.message}));
  }

  onToggleCreate = (success) => {
    if (success) {
      this.loadForecasts();
    }

    this.setState((prevState) => ({createFcModal: !prevState.createFcModal}));
  }

  onToggleAnalysis = (fcId) => {
  }

  onDeleteForecast = (fcId) => {
    const fcParams = {
      user: this.state.user,
      forecastId: fcId
    };

    deleteForecast(fcParams)
      .then(() => this.loadForecasts())
      .catch(({response: {data}}) => this.setState({error: data.message}));
  }

  render() {
    return !this.state.createFcModal ? (
      <Modal open size='large' dimmer='blurring'>
        <Modal.Content>
          <Modal.Header>
            <Header as='h1' icon textAlign='center'>
              <Icon name='wpforms' circular/>
              <Header.Content>My Forecasts</Header.Content>
            </Header>
          </Modal.Header>
          <Modal.Description>
            <Grid textAlign='center' verticalAlign='middle' columns={7}>
              <Grid.Column>
                <Header.Content as='h4'>Forecast ID</Header.Content>
              </Grid.Column>
              <Grid.Column>
                <Header.Content as='h4'>Forecast Date</Header.Content>
              </Grid.Column>
              <Grid.Column>
                <Header.Content as='h4'>Biodiversity Index</Header.Content>
              </Grid.Column>
              <Grid.Column>
                <Header.Content as='h4'>Stormwater Intercepted<br/>(L/YR)</Header.Content>
              </Grid.Column>
              <Grid.Column>
                <Header.Content as='h4'>CO2 Sequestrated<br/>(KG/YR)</Header.Content>
              </Grid.Column>
              <Grid.Column>
                <Header.Content as='h4'>Energy Conserved<br/>(KWH/YR)</Header.Content>
              </Grid.Column>
              <Grid.Column>
                <Header.Content as='h4'>Actions</Header.Content>
              </Grid.Column>
            </Grid>

            <Divider/>

            {!this.state.error && this.state.forecasts.length !== 0 ? (
              <Grid textAlign='center' verticalAlign='middle' columns={7}>
                {this.state.forecasts.map(({forecastId, fcDate, biodiversity, stormwater, co2Reduced, energyConserved}) => (
                  <Grid.Row key={forecastId}>
                    <Grid.Column>{forecastId}</Grid.Column>
                    <Grid.Column>{fcDate}</Grid.Column>
                    <Grid.Column>{biodiversity.toFixed(5)}</Grid.Column>
                    <Grid.Column>{stormwater.toFixed(2)}</Grid.Column>
                    <Grid.Column>{co2Reduced.toFixed(2)}</Grid.Column>
                    <Grid.Column>{energyConserved.toFixed(2)}</Grid.Column>
                    <Grid.Column>
                      <Button inverted circular size='mini' content='Analysis' color='blue' onClick={() => this.onToggleAnalysis(forecastId)}/>
                      <Button inverted circular size='mini' icon='delete' color='red' disabled={!this.state.user} onClick={() => this.onDeleteForecast(forecastId)}/>
                    </Grid.Column>
                  </Grid.Row>
                ))}
              </Grid>
            ) : this.state.error ? (
              <Message error>
                <Message.Header style={{textAlign: 'center'}}>{this.state.error}</Message.Header>
              </Message>
            ) : (
              <Message info>
                <Message.Header style={{textAlign: 'center'}}>Looks like you haven't created any forecasts yet!</Message.Header>
              </Message>
            )}

            <Divider hidden/>

            <Grid centered>
              <Grid.Row>
                <Button inverted color='green' size='small' disabled={!this.state.user} onClick={() => this.onToggleCreate(false)}>Create</Button>
                <Button inverted color='red' size='small' onClick={this.props.onClose}>Close</Button>
              </Grid.Row>
            </Grid>
          </Modal.Description>
        </Modal.Content>
      </Modal>
    ) : (
      <CreateForecastModal onClose={this.onToggleCreate}/>
    );
  }
}

MyForecastsModal.propTypes = {
  onClose: PropTypes.func.isRequired
};

export default MyForecastsModal;
