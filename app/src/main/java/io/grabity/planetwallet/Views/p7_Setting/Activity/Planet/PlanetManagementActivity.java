package io.grabity.planetwallet.Views.p7_Setting.Activity.Planet;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import java.util.ArrayList;
import java.util.Date;

import io.grabity.planetwallet.Common.commonset.C;
import io.grabity.planetwallet.Common.components.PlanetWalletActivity;
import io.grabity.planetwallet.MiniFramework.utils.Utils;
import io.grabity.planetwallet.R;
import io.grabity.planetwallet.VO.MainItems.BTC;
import io.grabity.planetwallet.VO.MainItems.CoinType;
import io.grabity.planetwallet.VO.MainItems.ERC20;
import io.grabity.planetwallet.VO.MainItems.ETH;
import io.grabity.planetwallet.VO.MainItems.MainItem;
import io.grabity.planetwallet.VO.Planet;
import io.grabity.planetwallet.Views.p3_Wallet.Activity.WalletAddActivity;
import io.grabity.planetwallet.Views.p4_Main.Adapter.MainAdapter;
import io.grabity.planetwallet.Views.p4_Main.Adapter.PlanetAdapter;
import io.grabity.planetwallet.Views.p7_Setting.Adapter.PlanetManagementAdapter;
import io.grabity.planetwallet.Widgets.AdvanceRecyclerView.AdvanceRecyclerView;
import io.grabity.planetwallet.Widgets.ToolBar;

public class PlanetManagementActivity extends PlanetWalletActivity implements AdvanceRecyclerView.OnItemClickListener, ToolBar.OnToolBarClickListener {

    private ViewMapper viewMapper;
    private ArrayList< Planet > items;
    private PlanetManagementAdapter adapter;

    @Override
    protected void onCreate( @Nullable Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_planet_management );
        viewMapper = new ViewMapper( );
        viewInit( );
        setData( );
    }

    @Override
    protected void viewInit( ) {
        super.viewInit( );

        viewMapper.toolBar.setLeftButton( new ToolBar.ButtonItem( ).setTag( C.tag.TOOLBAR_BACK ) );
        viewMapper.toolBar.setRightButton( new ToolBar.ButtonItem( ).setTag( C.tag.TOOLBAR_ADD ) );
        viewMapper.toolBar.setOnToolBarClickListener( this );
        viewMapper.listView.setOnItemClickListener( this );
    }

    @Override
    protected void setData( ) {
        super.setData( );
        /**
         * 임시작업
         */
        setDummy( );
    }

    void setDummy( ) {
        items = new ArrayList<>( );
        { // 1 Planet
            Planet planet = new Planet( );
            planet.setCoinType( CoinType.BTC );
            planet.setName( "Jacob Park" );
            planet.setAddress( "0x36072b48604d6d83b5bb304d36887b00213433d5" );
            items.add( planet );
        }

        {
            Planet planet = new Planet( );
            planet.setCoinType( CoinType.ETH );
            planet.setName( "Choi" );
            planet.setAddress( "0x501c94659d2c00b134a9ba418aa182f14bf72e56" );
            items.add( planet );
        }

        {
            Planet planet = new Planet( );
            planet.setCoinType( CoinType.BTC );
            planet.setName( "Jacob Park" );
            planet.setAddress( "0x43fedf6faf58a666b18f8cccebf0787b29591ede" );
            items.add( planet );
        }
        adapter = new PlanetManagementAdapter( getApplicationContext( ), items );
        viewMapper.listView.setAdapter( adapter );
    }

    @Override
    public void onToolBarClick( Object tag, View view ) {
        if ( Utils.equals( tag, C.tag.TOOLBAR_BACK ) ) {
            super.onBackPressed( );
        } else if ( Utils.equals( tag, C.tag.TOOLBAR_ADD ) ) {
            setTransition( Transition.SLIDE_UP );
            sendAction( C.requestCode.PLANET_ADD, WalletAddActivity.class );
        }
    }

    @Override
    public void onItemClick( AdvanceRecyclerView recyclerView, View view, int position ) {
        sendAction( DetailPlanetActivity.class, Utils.createSerializableBundle( C.bundleKey.PLANET, items.get( position ) ) );
    }


    public class ViewMapper {
        ToolBar toolBar;
        AdvanceRecyclerView listView;

        public ViewMapper( ) {
            toolBar = findViewById( R.id.toolBar );
            listView = findViewById( R.id.listView );
        }
    }
}
