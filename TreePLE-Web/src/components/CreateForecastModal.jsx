import React, {PureComponent} from 'react';
import PropTypes from 'prop-types';
import {compose, withProps} from 'recompose';
import {Button, Divider, Dropdown, Flag, Form, Grid, Header, Icon, List, Message, Modal} from 'semantic-ui-react';
import {GoogleMap, InfoWindow, Marker, withScriptjs, withGoogleMap} from 'react-google-maps';
import DayPickerInput from 'react-day-picker/DayPickerInput';
import {getAllTrees, createForecast} from "./Requests";
import {getError, formatDate} from './Utils';
import {gmapsKey, mtlCenter, huDates, flags} from '../constants';


class CreateForecastModal extends PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      user: localStorage.getItem('username'),
      fcDate: new Date(),
      fcTrees: [],
      trees: [],
      hover: null,
      language: 'en',
      error: ''
    };
  }

  componentWillMount() {
    getAllTrees()
      .then(({data}) => {
        this.setState({trees: data});
      })
      .catch(({response: {data}}) => {
        this.setState({error: data.message});
      });
  }

  onCreateForecast = () => {
    const fcParams = {
      fcUser: this.state.user,
      fcDate: formatDate(this.state.fcDate),
      fcTrees: this.state.fcTrees
    };

    createForecast(fcParams)
      .then(({data}) => {
        this.props.onClose();
      })
      .catch(({response: {data}}) => {
        this.setState({error: data.message});
      })
  }

  onRemoveTree = (tree) => {

  }

  onTreeClick = ({treeId}) => {
    let fcTrees = this.state.fcTrees.slice();
    if (!fcTrees.includes(treeId)) {
      fcTrees.push(treeId);
      this.setState({fcTrees: fcTrees});
    }
  }

  onTreeRightClick = ({treeId}) => {
    const hover = treeId == this.state.hover ? null : treeId;
    this.setState({hover: hover});
  }

  onDateChange = (day, {selected}) => this.setState({fcDate: day});
  onFlagChange = (e, {value}) => this.setState({language: value});

  render() {
    const {trees, fcTrees} = this.state;
    const errors = getError(this.state.error);

    const dayPickerProps = {
      locale: this.state.language,
      weekdaysShort: this.state.language == 'hu' ? huDates.weekShort : undefined,
      weekdaysLong: this.state.language == 'hu' ? huDates.weekLong : undefined,
      months: this.state.language == 'hu' ? huDates.months : undefined
    };

    return (
      <Modal open size='large' dimmer='blurring'>
        <Modal.Content>
          <Modal.Header>
            <Header as='h1' icon textAlign='center'>
              <Icon name='wpforms' circular/>
              <Header.Content>Create Forecast</Header.Content>
            </Header>
          </Modal.Header>
          <Modal.Description>
            <Form>
              <Form.Group widths='equal'>
                <Form.Input label='Date Planted' error={errors.date}>
                  <Dropdown compact selection options={flags} defaultValue={flags[0].key} onChange={this.onFlagChange}/>
                  <DayPickerInput placeholder='YYYY-MM-DD' format='YYYY-M-D' value={this.state.fcDate} dayPickerProps={dayPickerProps} onDayChange={this.onDateChange}/>
                </Form.Input>
                <Form.Input fluid readOnly label='Total Trees' placeholder='Number of Trees' type='number' min='0' value={fcTrees.length}/>
              </Form.Group>
            </Form>

            <Header as='h3' textAlign='center'>
              <Header.Content content='Tree Map'/>
            </Header>
            <Divider/>
            <Grid centered>
              <Grid.Row>
                <Button inverted color='blue' size='small' disabled={!this.state.user} onClick={this.props.onForecast}>Edit</Button>
                <Button inverted color='green' size='small' disabled={!this.state.user} onClick={this.onCreateForecast}>Save</Button>
                {/* <Dropdown inverted color='orange' size='small' onClick={this.onToggleEdit}>Back</Dropdown> */}
              </Grid.Row>
            </Grid>

            <Divider hidden/>
            <GMap trees={trees} hover={this.state.hover} onTreeClick={this.onTreeClick} onTreeRightClick={this.onTreeRightClick}/>
            <Divider hidden/>

            {this.state.error ? (
              <Message error>
                <Message.Header style={{textAlign: 'center'}}>{this.state.error}</Message.Header>
              </Message>
            ) : null}

            <Grid centered>
              <Grid.Row>
                <Button inverted color='green' size='small' disabled={!this.state.user} onClick={this.onCreateForecast}>Create</Button>
                <Button inverted color='orange' size='small' onClick={this.props.onForecast}>Back</Button>
                <Button inverted color='red' size='small' onClick={this.props.onClose}>Close</Button>
              </Grid.Row>
            </Grid>
          </Modal.Description>
        </Modal.Content>
      </Modal>
    );
  }
}

const GMap = compose(
  withProps({
    googleMapURL: `https://maps.googleapis.com/maps/api/js?key=${gmapsKey}&v=3.exp&libraries=geometry,drawing,places`,
    loadingElement: <div style={{width: '100vw', height: '60vh'}}/>,
    containerElement: <div style={{height: '60vh'}}/>,
    mapElement: <div style={{height: '60vh'}}/>,
  }),
  withScriptjs,
  withGoogleMap
)(({trees, hover, onTreeClick, onTreeRightClick}) => {

  return (
    <GoogleMap zoom={14} center={mtlCenter} options={{scrollwheel: true}}>
      {trees.map(tree => {
        return (
          <Marker
            key={tree.treeId}
            position={{lat: tree.location.latitude, lng: tree.location.longitude}}
            onClick={() => onTreeClick(tree)}
            onRightClick={() => onTreeRightClick(tree)}
          >
            {(hover && hover == tree.treeId) ? (
              <InfoWindow onCloseClick={() => onTreeRightClick(null)}>
                <List horizontal>
                  <List.Item icon='tree'/>
                  <List.Item content={treeId}/>
                </List>
              </InfoWindow>
            ) : null}
          </Marker>
        );
      })}
    </GoogleMap>
  );
});

CreateForecastModal.propTypes = {
  onForecast: PropTypes.func.isRequired,
  onClose: PropTypes.func.isRequired
}

export default CreateForecastModal;
